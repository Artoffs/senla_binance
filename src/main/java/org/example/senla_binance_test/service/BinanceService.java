package org.example.senla_binance_test.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.dto.PriceResponseDto;
import org.example.senla_binance_test.exception.ResourceNotFoundException;
import org.example.senla_binance_test.mapper.BinanceMapper;
import org.example.senla_binance_test.repository.BinanceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceService {

    private final BinanceRepository repository;

    @Transactional
    public void saveIfNotExists(String symbol, BinancePriceResponse response) {
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(response.closeTime()), ZoneOffset.UTC);

        if(repository.existsBySymbolAndCloseTime(symbol, offsetDateTime)) {
            log.error("Такая запись уже существует в бд: {}", response);
            return;
        }

        repository.save(BinanceMapper.toEntity(response, symbol));
    }

    public PriceResponseDto getLatest() {
        return repository.findFirstByOrderByFetchedAtDesc()
                .map(BinanceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Нет данных по выбранной паре"));
    }
}
