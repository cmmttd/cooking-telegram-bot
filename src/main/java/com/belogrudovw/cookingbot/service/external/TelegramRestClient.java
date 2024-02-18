package com.belogrudovw.cookingbot.service.external;

import com.belogrudovw.cookingbot.domain.telegram.Keyboard;
import com.belogrudovw.cookingbot.domain.telegram.TelegramResponse;
import com.belogrudovw.cookingbot.service.ResponseService;
import com.belogrudovw.cookingbot.util.CustomUriBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.belogrudovw.cookingbot.util.StringUtil.escapeCharacters;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramRestClient implements ResponseService {

    private final WebClient telegramWebClient;

    @Override
    public Mono<TelegramResponse> sendMessage(long chatId, String text, Keyboard keyboard) {
        String uri = CustomUriBuilder.builder()
                .path("/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", escapeCharacters(text))
                .queryParam("parse_mode", "MarkdownV2")
                .queryParam("reply_markup", keyboard.toString())
                .build();
        return telegramPostCall(uri, chatId);
    }

    @Override
    public Mono<TelegramResponse> editMessage(long chatId, long messageId, String text, Keyboard keyboard) {
        String uri = CustomUriBuilder.builder()
                .path("/editMessageText")
                .queryParam("chat_id", chatId)
                .queryParam("message_id", messageId)
                .queryParam("text", escapeCharacters(text))
                .queryParam("parse_mode", "MarkdownV2")
                .queryParam("reply_markup", keyboard.toString())
                .build();
        return telegramPostCall(uri, chatId);
    }

    @Override
    public Mono<TelegramResponse> saveImage(byte[] file, String description) {
        ByteArrayResource byteArrayResource = new ByteArrayResource(file) {
            @Override
            public String getFilename() {
                return "image.png";
            }
        };
        var bodyInserters = BodyInserters
                .fromMultipartData("chat_id", "-1002012962538")
                .with("caption", escapeCharacters(description))
                .with("photo", byteArrayResource);
        return telegramWebClient.post()
                .uri("/sendPhoto")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(bodyInserters)
                .retrieve()
                .bodyToMono(TelegramResponse.class)
                .doOnError(e -> log.error("Failed image save to telegram"))
                .doOnSuccess(resp -> log.debug("Image saved to telegram: {}", resp));
    }

    @Override
    public Mono<TelegramResponse> sendImage(long chatId, String text, Keyboard keyboard, String imageId) {
        String uri = CustomUriBuilder.builder()
                .path("/sendPhoto")
                .queryParam("chat_id", chatId)
                .queryParam("photo", imageId)
                .build();
        return telegramWebClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(TelegramResponse.class)
                .doOnError(e -> log.error("Failed image send to chat: {}", chatId))
                .doOnSuccess(resp -> log.debug("Image saved to chat: {} {} ", chatId, resp));
    }

    @Override
    public Mono<TelegramResponse> editImage(long chatId, long messageId, String text, Keyboard keyboard, String imageId) {
        String jsonMedia = "{\"type\":\"photo\",\"media\":\"%s\"}";
        String uri = CustomUriBuilder.builder()
                .path("/editMessageMedia")
                .queryParam("chat_id", chatId)
                .queryParam("message_id", messageId)
                .queryParam("media", jsonMedia.formatted(imageId))
                .build();
        return telegramWebClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(TelegramResponse.class)
                .doOnError(e -> log.error("Failed image save to telegram"))
                .doOnSuccess(resp -> log.debug("Image saved to telegram: {}", resp));
    }

    private Mono<TelegramResponse> telegramPostCall(String uri, long chatId) {
        return telegramWebClient.post()
                .uri(uri)
                .exchangeToMono(resp -> resp.bodyToMono(TelegramResponse.class))
                .doOnError(e -> log.error("Failed telegram api call for chatId: {}", chatId))
                .doOnSuccess(resp -> log.debug("Succeed telegram api call for chatId: {}. Response {}", chatId, resp));
    }
}