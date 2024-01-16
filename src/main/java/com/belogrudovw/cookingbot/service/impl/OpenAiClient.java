package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.config.OpenAiProperties;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.RequestPreferences;
import com.belogrudovw.cookingbot.domain.displayable.Languages;
import com.belogrudovw.cookingbot.service.RecipeSupplier;
import com.belogrudovw.cookingbot.storage.Storage;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OpenAiClient implements RecipeSupplier {

    // TODO: 08/01/2024 Build request with dto
    static final String REQUEST_PATTERN = """
            {
              "model": \"${model}\",
              "messages": [
                {
                  "role": "system",
                  "content": "You are cooking assistant. Your aim to provide to users usable, interesting and uniq recipes by requested parameters. Response must be only one valid json object and precisely follow the pattern: {\\"language\\":%s Language from the recipes request,\\"properties\\":{\\"lightness\\":%s(same as request - Light or Moderate or Heavy),\\"units\\":%s (same as request - METRIC or IMPERIAL),\\"cooking_time\\":%d general time of cooking process in minutes for the recipe},\\"title\\":%s,\\"short_description\\":%s 100-500 symbols,\\"ingredients\\":[%s],\\"steps\\":[{\\"index\\":%d sequential number from one,\\"offset\\":%d in minutes related to the first step (from 0 to end cooking time),\\"title\\":%s step title,\\"description\\":%s what should be done}]}"
                },
                {
                  "role": "user",
                  "content": "One more recipe please by parameters: whole recipe language (except properties) must be ${language}, relative lightness of the recipe by energy value - ${lightness}, measurement units - ${units}, cooking time less than ${difficulty} minutes.${additional_query} Recipe must to fit to each parameter precisely, please pay a lot attention to follow them"
                }
              ],
              "temperature": 1.25,
              "max_tokens": 4000,
              "top_p": 1,
              "frequency_penalty": 0,
              "presence_penalty": 0
            }
            """;
    static final int MAX_ATTEMPTS = 3;
    static final int RESPONSE_TIMEOUT_MIN = 10;

    WebClient openAiWebClient;
    OpenAiProperties properties;
    ObjectMapper objectMapper;
    Storage<UUID, Recipe> recipeStorage;

    @Override
    public Mono<Recipe> get(RequestPreferences request, String additionalQuery) throws RuntimeException {
        String formatted = prepareRequestBody(request, additionalQuery);
        return openAiWebClient.post()
                .bodyValue(formatted)
                .exchangeToMono(resp -> resp.bodyToMono(OpenApiResponse.class))
                .doOnSuccess(response -> log.debug("<<< Succeed openai response: {}", response))
                .doOnError(err -> {
                    // TODO: 11/01/2024 Add more specific prompt-request if error
                    log.error("Recipe request failed", err);
                    throw new RuntimeException(err);
                })
                .map(this::parseRecipe)
                .map(this::duplicatesCheck)
                .timeout(Duration.ofMinutes(RESPONSE_TIMEOUT_MIN))
                .retryWhen(setupRetry())
                .doOnError(err -> log.error("Reties weren't complete by {} attempts", MAX_ATTEMPTS, err))
                .onErrorReturn(getStubRecipe());
    }

    private String prepareRequestBody(RequestPreferences request, String additionalQuery) {
        OpenAiProperties.Models models = properties.conversation().models();
        String additionalQueryReplacement = additionalQuery.isBlank()
                ? ""
                : "Also consider additional requirements from user: %s".formatted(additionalQuery);
        String modelReplacement = Set.of(Languages.CH, Languages.JP, Languages.RU, Languages.FR, Languages.EN)
                .contains(request.getLanguage()) ? models.wise() : models.cheap();
        return REQUEST_PATTERN
                .replaceAll("\\$\\{model}", modelReplacement)
                .replaceAll("\\$\\{language}", request.getLanguage().getLangName())
                .replaceAll("\\$\\{lightness}", request.getLightness().name())
                .replaceAll("\\$\\{units}", request.getUnits().name())
                .replaceAll("\\$\\{difficulty}", String.valueOf(request.getDifficulty().getMinutes()))
                .replaceAll("\\$\\{additional_query}", additionalQueryReplacement);
    }

    private Recipe parseRecipe(OpenApiResponse resp) {
        String content = resp.choices().get(0).message().content();
        int beginIndex = content.indexOf("{");
        content = content.substring(beginIndex)
                .replaceAll("```json", "")
                .replaceAll("```", "");
        if (!content.startsWith("{")) {
            log.warn("<<<< Invalid json: {}", content.split("\\{")[0].replaceAll("\\n", ""));
        }
        try {
            return objectMapper.readValue(content, Recipe.class);
        } catch (JsonProcessingException e) {
            log.error("Parsing error during the openai request", e);
            throw new RuntimeException(e);
        }
    }

    private Recipe duplicatesCheck(Recipe recipe) {
        boolean isAbsent = recipeStorage.all()
                .parallel()
                .map(Recipe::getTitle)
                .filter(current -> current.equalsIgnoreCase(recipe.getTitle()))
                .findAny()
                .isEmpty();
        if (isAbsent) {
            recipeStorage.save(recipe);
            log.info("New recipe has been generated and saved: {}", recipe.getTitle());
            return recipe;
        } else {
            log.error("Recipe already exists: {}", recipe.getTitle());
            throw new RuntimeException();
        }
    }

    private static RetryBackoffSpec setupRetry() {
        return Retry.backoff(MAX_ATTEMPTS, Duration.ofSeconds(2))
                .jitter(0d)
                .doAfterRetry(retrySignal -> log.info("Retried {}", retrySignal.totalRetries() + 1))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new IllegalArgumentException());
    }

    record OpenApiResponse(List<Choice> choices) {

        record Choice(int index, Message message) {
        }

        record Message(String content) {
        }
    }
}