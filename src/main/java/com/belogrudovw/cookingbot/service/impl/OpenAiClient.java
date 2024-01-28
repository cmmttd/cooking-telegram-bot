package com.belogrudovw.cookingbot.service.impl;

import com.belogrudovw.cookingbot.config.OpenAiProperties;
import com.belogrudovw.cookingbot.domain.Recipe;
import com.belogrudovw.cookingbot.domain.RequestPreferences;
import com.belogrudovw.cookingbot.exception.RecipeGenerationException;
import com.belogrudovw.cookingbot.service.RecipeSupplier;
import com.belogrudovw.cookingbot.storage.Storage;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import static com.belogrudovw.cookingbot.util.JsonTemplates.OPEN_AI_RECIPE_REQUEST_TEMPLATE;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OpenAiClient implements RecipeSupplier {

    static final int MAX_ATTEMPTS = 5;
    static final int RESPONSE_TIMEOUT_MIN = 3;

    WebClient openAiWebClient;
    OpenAiProperties properties;
    ObjectMapper objectMapper;
    Storage<UUID, Recipe> recipeStorage;

    @Override
    public Mono<Recipe> get(RequestPreferences request, String additionalQuery) throws RuntimeException {
        Set<String> alreadyRequestedRecipes = new ConcurrentSkipListSet<>();
        return enhanceableRequest(request, additionalQuery, alreadyRequestedRecipes)
                .retryWhen(setupRetry())
                .doOnError(err -> log.error("Retries weren't complete by {} attempts", MAX_ATTEMPTS, err))
                .onErrorReturn(getStubRecipe(request.getLanguage()));
    }

    private Mono<Recipe> enhanceableRequest(RequestPreferences request, String additionalQuery, Set<String> alreadyRequested) {
        // TODO: 05/02/2024 Make requests in parallel
        return Mono.fromCallable(() -> prepareRequestBody(request, additionalQuery, alreadyRequested))
                .flatMap(this::apiCall)
                .flatMap(recipe -> {
                    if (isUnique(recipe)) {
                        recipeStorage.save(recipe);
                        return Mono.just(recipe);
                    } else {
                        log.warn("Recipe already exists: {}", recipe.getTitle());
                        alreadyRequested.add(recipe.getTitle());
                        return Mono.error(new RecipeGenerationException("Generated recipe already exists"));
                    }
                });
    }

    private boolean isUnique(Recipe recipe) {
        Predicate<Recipe> comparisonPredicate = current -> current.getTitle().equalsIgnoreCase(recipe.getTitle())
                && current.getSteps().size() == recipe.getSteps().size();
        return recipeStorage.all()
                .parallel()
                .anyMatch(comparisonPredicate);
    }

    private Mono<Recipe> apiCall(String requestBody) {
        return openAiWebClient.post()
                .bodyValue(requestBody)
                .exchangeToMono(resp -> resp.bodyToMono(OpenApiResponse.class))
                .subscribeOn(Schedulers.parallel())
                .timeout(Duration.ofMinutes(RESPONSE_TIMEOUT_MIN))
                .doOnError(err -> {
                    String exceptionMessage = "OpenAi invocation failed";
                    log.error(exceptionMessage, err);
                    throw new RecipeGenerationException(exceptionMessage, err);
                })
                .map(this::parseRecipe);
    }

    private String prepareRequestBody(RequestPreferences request, String additionalQuery, Set<String> alreadyRequested) {
        // TODO: 04/02/2024 Replace by pojo build
        OpenAiProperties.Conversation conversation = properties.conversation();
        String temperature = conversation.temperature();
        String additionalQueryReplacement = additionalQuery.isBlank()
                ? ""
                : " Also consider additional requirements from user: %s.".formatted(additionalQuery);
        if (!alreadyRequested.isEmpty()) {
            additionalQueryReplacement += " Reveal your fantasy.";
            additionalQueryReplacement += " And not from the list: %s.".formatted(String.join(", ", alreadyRequested));
            temperature = "1.4";
        }
        return OPEN_AI_RECIPE_REQUEST_TEMPLATE.getJson()
                .replaceAll("\\$\\{model}", conversation.models().wise())
                .replaceAll("\\$\\{temperature}", temperature)
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

    private static RetryBackoffSpec setupRetry() {
        return Retry.backoff(MAX_ATTEMPTS, Duration.ofSeconds(1))
                .jitter(0.75)
                .filter(RecipeGenerationException.class::isInstance)
                .doAfterRetry(retrySignal -> log.info("Retried {}", retrySignal.totalRetries() + 1))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new RecipeGenerationException("Retry limit exceeded"));
    }

    record OpenApiResponse(List<Choice> choices) {

        record Choice(int index, Message message) {
        }

        record Message(String content) {
        }
    }
}