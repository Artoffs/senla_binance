package org.example.senla_binance_test.exception;


import java.time.OffsetDateTime;

public record ErrorResponse(
        OffsetDateTime time,
        String message
) {
}
