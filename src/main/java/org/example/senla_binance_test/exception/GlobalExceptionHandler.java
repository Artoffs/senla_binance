package org.example.senla_binance_test.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ErrorResponse handleException(Exception e) {
        return new ErrorResponse(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC), e.getMessage());
    }
}
