package org.example.senla_binance_test.dto;

import java.math.BigDecimal;

public record BinancePriceResponse(
        String mins,
        BigDecimal price,
        Long closeTime
) {}