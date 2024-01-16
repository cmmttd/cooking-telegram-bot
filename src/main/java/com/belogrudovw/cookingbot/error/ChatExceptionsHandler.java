package com.belogrudovw.cookingbot.error;

import com.belogrudovw.cookingbot.handler.Handler;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatExceptionsHandler {

    private final Handler defaultHandler;

    @ExceptionHandler(IllegalChatStateException.class)
    public boolean handleIllegalChatStateException(IllegalChatStateException ex) {
        log.error(ex.getMessage(), ex);
        defaultHandler.handle(ex.getChat());
        return true;
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ProblemDetail handleValidationExceptions(WebExchangeBindException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NO_CONTENT, ex.getMessage());
        String title = "Constraints violation";
        problemDetail.setTitle(title);
        Map<String, Object> violations = ex.getBindingResult().getAllErrors()
                .stream()
                .filter(e -> e.getDefaultMessage() != null)
                .collect(Collectors.toMap(e -> ((FieldError) e).getField(), DefaultMessageSourceResolvable::getDefaultMessage));
        problemDetail.setProperties(violations);
        log.error(title, ex);
        return problemDetail;
    }
}