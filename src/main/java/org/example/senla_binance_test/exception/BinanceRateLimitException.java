package org.example.senla_binance_test.exception;

import lombok.Getter;

@Getter
public class BinanceRateLimitException extends RuntimeException {
    private final long retryAfterSeconds;

    public BinanceRateLimitException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
