package org.example.senla_binance_test.service;

import org.example.senla_binance_test.dto.AveragePriceResponseDto;
import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.dto.PriceResponseDto;
import org.example.senla_binance_test.exception.ResourceNotFoundException;
import org.example.senla_binance_test.model.BinanceResponseEntity;
import org.example.senla_binance_test.repository.BinanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BinanceServiceTest {

    private String defaultSymbol = "BNBUSDT";

    @Mock
    private BinanceRepository repository;

    @InjectMocks
    private BinanceService service;

    @BeforeEach
    public void setUp() {
        service = new BinanceService(repository, defaultSymbol);
    }

    @Test
    void saveIfNotExists_happyPath() {

        BinancePriceResponse response = new BinancePriceResponse("0", BigDecimal.valueOf(1L), 1L);

        Mockito.when(repository.existsBySymbolAndCloseTime(defaultSymbol,
                OffsetDateTime.ofInstant(Instant.ofEpochMilli(response.closeTime()), ZoneOffset.UTC)))
                .thenReturn(false);

        service.saveIfNotExists(response);

        Mockito.verify(repository, times(1)).save(any(BinanceResponseEntity.class));
    }

    @Test
    void saveIfNotExists_alreadyExists() {

        BinancePriceResponse response = new BinancePriceResponse("0", BigDecimal.valueOf(1L), 1L);

        Mockito.when(repository.existsBySymbolAndCloseTime(defaultSymbol,
                        OffsetDateTime.ofInstant(Instant.ofEpochMilli(response.closeTime()), ZoneOffset.UTC)))
                .thenReturn(true);

        service.saveIfNotExists(response);

        Mockito.verify(repository, times(0)).save(any(BinanceResponseEntity.class));
    }

    @Test
    void getLatest_happyPath() {

        BinanceResponseEntity value = new BinanceResponseEntity();
        value.setCloseTime(OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC));
        value.setPrice(BigDecimal.valueOf(10f));
        value.setFetchedAt(OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC));
        value.setSymbol(defaultSymbol);
        value.setId(1L);

        Mockito.when(repository.findFirstBySymbolOrderByFetchedAtDesc(defaultSymbol)).thenReturn(
                Optional.of(value));

        PriceResponseDto latest = service.getLatest();

        Assertions.assertEquals(BigDecimal.valueOf(10f), latest.price());
    }

    @Test
    void getLatest_empty() {

        Mockito.when(repository.findFirstBySymbolOrderByFetchedAtDesc(defaultSymbol)).thenReturn(
                Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.getLatest());
    }

    @Test
    void getAverage_happyPath() {
        OffsetDateTime from = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1L), ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.ofInstant(Instant.ofEpochMilli(2L), ZoneOffset.UTC);

        Mockito.when(repository.getAverageForPeriod(defaultSymbol, from, to))
                .thenReturn(Optional.of(BigDecimal.valueOf(10)));

        AveragePriceResponseDto average = service.getAverage(from, to);

        assertThat(average.averagePrice().compareTo(BigDecimal.valueOf(10))).isZero();
    }

    @Test
    void getAverage_empty() {
        OffsetDateTime from = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1L), ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.ofInstant(Instant.ofEpochMilli(2L), ZoneOffset.UTC);

        Mockito.when(repository.getAverageForPeriod(defaultSymbol, from, to))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.getAverage(from, to));
    }

    @Test
    void getAll() {
    }

    @Test
    void getTrend() {
    }
}