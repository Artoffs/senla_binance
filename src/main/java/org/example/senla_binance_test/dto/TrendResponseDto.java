package org.example.senla_binance_test.dto;

import java.time.OffsetDateTime;

public record TrendResponseDto(
        String symbol,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        TrendType trend
) {}