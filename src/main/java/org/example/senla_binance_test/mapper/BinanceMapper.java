package org.example.senla_binance_test.mapper;

import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.model.BinanceResponseEntity;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public final class BinanceMapper {

    private BinanceMapper() {

    }

    public static BinanceResponseEntity toEntity(BinancePriceResponse response, String symbol) {
        BinanceResponseEntity entity = new BinanceResponseEntity();

        entity.setCloseTime(OffsetDateTime.ofInstant(
                Instant.ofEpochMilli(response.closeTime()), ZoneId.systemDefault()));

        entity.setPrice(response.price());

        entity.setSymbol(symbol);

        entity.setFetchedAt(OffsetDateTime.now(ZoneId.systemDefault()));

        return entity;
    }
}
