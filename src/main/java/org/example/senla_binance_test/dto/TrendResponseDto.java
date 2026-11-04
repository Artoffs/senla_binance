package org.example.senla_binance_test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TrendResponseDto(
        String symbol,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        TrendType trend
) {}