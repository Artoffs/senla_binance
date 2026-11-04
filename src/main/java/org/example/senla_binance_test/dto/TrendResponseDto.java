package org.example.senla_binance_test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record TrendResponseDto(
        String symbol,
        @JsonProperty("start_date") LocalDate startDate,
        @JsonProperty("end_date") LocalDate endDate,
        TrendType trend
) {}