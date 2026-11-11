package org.example.senla_binance_test.mapper;

import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.dto.PriceResponseDto;
import org.example.senla_binance_test.model.BinanceResponseEntity;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public final class BinanceMapper {

    private BinanceMapper() {

    }

    public static BinanceResponseEntity toEntity(BinancePriceResponse response, String symbol) {
        BinanceResponseEntity entity = new BinanceResponseEntity();

        entity.setSymbol(symbol);
        entity.setPrice(response.price());
        entity.setCloseTime(OffsetDateTime.ofInstant(
                Instant.ofEpochMilli(response.closeTime()), ZoneOffset.UTC));
        entity.setFetchedAt(OffsetDateTime.now(ZoneOffset.UTC));

        return entity;
    }

    public static PriceResponseDto toDto(BinanceResponseEntity entity) {
        return new PriceResponseDto(
                entity.getSymbol(),
                entity.getPrice(),
                entity.getFetchedAt().withOffsetSameInstant(ZoneOffset.UTC)
        );
    }
}
