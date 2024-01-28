package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.config.OpenAiProperties;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.RequestPreferences;
import com.belogrudovw.cookingbot.exception.RecipeGenerationException;
import com.belogrudovw.cookingbot.service.RecipeSupplier;
import com.belogrudovw.cookingbot.storage.Storage;

import java.time.Duration;
import java.util.List;
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

import static com.belogrudovw.cookingbot.util.JsonTemplates.OPEN_AI_RECIPE_REQUEST_TEMPLATE;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OpenAiClient implements RecipeSupplier {

    static final int MAX_ATTEMPTS = 3;
    static final int RESPONSE_TIMEOUT_MIN = 10;

    WebClient openAiWebClient;
    OpenAiProperties properties;
    ObjectMapper objectMapper;
    Storage<UUID, Recipe> recipeStorage;

    @Override
    public Mono<Recipe> get(RequestPreferences request, String additionalQuery) throws RuntimeException {
        // TODO: 27/01/2024 Combine normal request and extended for retry request into pipeline and wrap back to retry
        String formatted = prepareRequestBody(request, additionalQuery);
        return openAiWebClient.post()
                .bodyValue(formatted)
                .exchangeToMono(resp -> resp.bodyToMono(OpenApiResponse.class))
                .doOnSuccess(response -> log.debug("<<< Succeed openai response: {}", response))
                .doOnError(err -> {
                    // TODO: 11/01/2024 Add more specific prompt-request if error
                    log.error("Recipe request failed", err);
                    throw new RecipeGenerationException("Api call failed", err);
                })
                .map(this::parseRecipe)
                .map(this::duplicatesCheck)
                .timeout(Duration.ofMinutes(RESPONSE_TIMEOUT_MIN))
                .retryWhen(setupRetry())
                .doOnError(err -> log.error("Reties weren't complete by {} attempts", MAX_ATTEMPTS, err))
                .onErrorReturn(getStubRecipe(request.getLanguage()));
    }

    private String prepareRequestBody(RequestPreferences request, String additionalQuery) {
        // TODO: 04/02/2024 Replace by pojo build
        OpenAiProperties.Conversation conversation = properties.conversation();
        String additionalQueryReplacement = additionalQuery.isBlank()
                ? ""
                : " Also consider additional requirements from user: %s.".formatted(additionalQuery);
        return OPEN_AI_RECIPE_REQUEST_TEMPLATE.getJson()
                .replaceAll("\\$\\{model}", conversation.models().wise())
                .replaceAll("\\$\\{temperature}", conversation.temperature())
                .replaceAll("\\$\\{max-tokens}", conversation.maxTokens())
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
            String errorMessage = "Parsing error during the openai request";
            log.error(errorMessage, e);
            throw new RecipeGenerationException(errorMessage, e);
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
            String errorMessage = "Recipe already exists: " + recipe.getTitle();
            log.error(errorMessage);
            throw new RecipeGenerationException(errorMessage);
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