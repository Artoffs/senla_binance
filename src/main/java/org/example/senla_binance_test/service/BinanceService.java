package org.example.senla_binance_test.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.senla_binance_test.dto.AveragePriceResponseDto;
import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.dto.PriceResponseDto;
import org.example.senla_binance_test.exception.ResourceNotFoundException;
import org.example.senla_binance_test.mapper.BinanceMapper;
import org.example.senla_binance_test.repository.BinanceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceService {

    private final BinanceRepository repository;

    @Value("${binance.default-symbol:BNBUSDT}")
    private String defaultSymbol;

    @Transactional
    public void saveIfNotExists(BinancePriceResponse response) {
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(response.closeTime()), ZoneOffset.UTC);

        if(repository.existsBySymbolAndCloseTime(defaultSymbol, offsetDateTime)) {
            log.error("Такая запись уже существует в бд: {}", response);
            return;
        }

        repository.save(BinanceMapper.toEntity(response, defaultSymbol));
    }

    @Transactional(readOnly = true)
    public PriceResponseDto getLatest() {
        return repository.findFirstBySymbolOrderByFetchedAtDesc(defaultSymbol)
                .map(BinanceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Нет данных по выбранной паре"));
    }

    @Transactional(readOnly = true)
    public AveragePriceResponseDto getAverage(OffsetDateTime from, OffsetDateTime to) {
        BigDecimal avg = repository.getAverageForPeriod(defaultSymbol, from, to).orElseThrow(
                () -> new ResourceNotFoundException("Записей в заданном интервале не найдено")
        );
        return new AveragePriceResponseDto(avg.setScale(8, RoundingMode.HALF_UP));
    }
}
