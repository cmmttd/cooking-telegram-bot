package com.belogrudovw.cookingbot.service.external;

import com.belogrudovw.cookingbot.config.ResilienceProperties;
import com.belogrudovw.cookingbot.config.StableDiffusionProperties;
import com.belogrudovw.cookingbot.service.ImageSupplier;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import static com.belogrudovw.cookingbot.util.JsonTemplates.SD_IMAGE_REQUEST_TEMPLATE;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StableDiffusionClient implements ImageSupplier {

    WebClient sdWebClient;
    StableDiffusionProperties stableDiffusionProperties;
    ResilienceProperties resilienceProperties;

    @Override
    public Mono<byte[]> getImageByText(String request) {
        String requestBody = SD_IMAGE_REQUEST_TEMPLATE.getJson()
                .replaceAll("\\$\\{recipeEngString}", request);
        return sdWebClient.post()
                .uri(stableDiffusionProperties.path())
                .bodyValue(requestBody)
                .accept(MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(byte[].class)
                .subscribeOn(Schedulers.parallel())
                .timeout(Duration.ofMinutes(resilienceProperties.timeoutMinutes()))
                .retryWhen(setupRetry())
                .doOnError(err -> {
                    String exceptionMessage = "Stable Diffusion API invocation failed";
                    log.error(exceptionMessage, err);
                    // TODO: 18/02/2024 Handle by custom exceptions
                    throw new RuntimeException(exceptionMessage, err);
                })
                .publishOn(Schedulers.boundedElastic());
    }

    private RetryBackoffSpec setupRetry() {
        return Retry.backoff(resilienceProperties.retry().count(), Duration.ofSeconds(resilienceProperties.retry().delaySeconds()))
                .jitter(resilienceProperties.retry().jitter())
                // TODO: 13/02/2024 Add custom exception
                .filter(RuntimeException.class::isInstance)
                .doAfterRetry(retrySignal -> log.info("Retried {}", retrySignal.totalRetries() + 1))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new RuntimeException("Retry limit exceeded"));
    }
}