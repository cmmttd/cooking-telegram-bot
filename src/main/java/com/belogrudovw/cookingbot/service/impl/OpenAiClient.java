package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.config.OpenAiProperties;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.RequestProperties;
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
                  "content": "You are cooking assistant. Your aim to provide to users usable, interesting and uniq recipes by requested parameters"
                },
                {
                  "role": "user",
                  "content": "Recipe must to fit to each parameter precisely, please pay a lot attention to follow them - whole recipe language (except properties) must be ${language}, relative lightness of the recipe by energy value - ${lightness}, measurement units - ${units}, cooking time less than ${difficulty} minutes. Response must be only one valid json object and precisely follow the pattern: {\\"language\\":%s Language from the recipes request,\\"properties\\":{\\"lightness\\":%s(same as request - Light or Moderate or Heavy),\\"units\\":%s (same as request - METRIC or IMPERIAL),\\"cooking_time\\":%d general time of cooking process in minutes for the recipe},\\"title\\":%s,\\"short_description\\":%s 100-500 symbols,\\"ingredients\\":[%s],\\"steps\\":[{\\"index\\":%d sequential number from one,\\"offset\\":%d in minutes related to the first step (from 0 to end cooking time),\\"title\\":%s step title,\\"description\\":%s what should be done}]}"
                }
              ],
              "temperature": 1,
              "max_tokens": 4000,
              "top_p": 1,
              "frequency_penalty": 0,
              "presence_penalty": 0
            }
            """;

    WebClient openAiWebClient;
    OpenAiProperties properties;
    ObjectMapper objectMapper;
    Storage<UUID, Recipe> recipeStorage;

    @Override
    public Mono<Recipe> get(RequestProperties request) throws RuntimeException {
        OpenAiProperties.Models models = properties.conversation().models();
        String formatted = REQUEST_PATTERN
                .replaceAll("\\$\\{model}", Set.of(Languages.CH, Languages.JP, Languages.RU, Languages.FR)
                        .contains(request.getLanguage()) ? models.wise() : models.cheap())
                .replaceAll("\\$\\{language}", request.getLanguage().getText())
                .replaceAll("\\$\\{lightness}", request.getLightness().getText())
                .replaceAll("\\$\\{units}", request.getUnits().getText())
                .replaceAll("\\$\\{difficulty}", String.valueOf(request.getDifficulty().getMinutes()));
        return openAiWebClient.post()
                .bodyValue(formatted)
                .exchangeToMono(resp -> resp.bodyToMono(OpenApiResponse.class))
                .doOnSuccess(response -> log.debug("<<< Succeed openai response: {}", response))
                .doOnError(err -> {
                    log.error("Recipe request failed", err);
                    throw new RuntimeException(err);
                })
                .map(resp -> {
                    String content = resp.choices().get(0).message().content();
                    int beginIndex = content.indexOf("{");
                    if (beginIndex != 0) {
                        log.info("<<<< Invalid json: {}", content.split("\\{")[0].replaceAll("\\n", ""));
                    }
                    content = content.substring(beginIndex)
                            .replaceAll("```json", "")
                            .replaceAll("```", "");
                    try {
                        Recipe recipe = objectMapper.readValue(content, Recipe.class);
                        return recipe;
                    } catch (JsonProcessingException e) {
                        log.error("Parsing error during the openai request", e);
                        throw new RuntimeException(e);
//                        return getStubRecipe();
                    }
                })
                .map(recipe -> {
                    boolean isAbsent = recipeStorage.all()
                            .parallel()
                            .map(Recipe::getTitle)
                            .filter(current -> current.equalsIgnoreCase(recipe.getTitle()))
                            .findAny()
                            .isEmpty();
                    if (isAbsent) {
                        recipeStorage.save(recipe);
                        log.info("New recipe has been generated: {}", recipe.getTitle());
                        return recipe;
                    } else {
                        log.error("Recipe exists: {}", recipe.getTitle());
                        throw new RuntimeException();
                    }
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .jitter(0d)
                        .doAfterRetry(retrySignal -> log.info("Retried " + retrySignal.totalRetries()))
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new IllegalArgumentException()));
    }

    record OpenApiResponse(List<Choice> choices) {

        record Choice(int index, Message message) {
        }

        record Message(String content) {
        }
    }
}