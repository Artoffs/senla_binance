package org.example.senla_binance_test.dto;

import java.util.List;

public record PageResponseDto<T>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        boolean hasNext
) {}
