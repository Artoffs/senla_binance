package org.example.senla_binance_test.service;

import org.example.senla_binance_test.dto.AveragePriceResponseDto;
import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.dto.PageResponseDto;
import org.example.senla_binance_test.dto.PriceResponseDto;
import org.example.senla_binance_test.dto.TrendResponseDto;
import org.example.senla_binance_test.dto.TrendType;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BinanceServiceTest {

    private String defaultSymbol = "BNBUSDT";

    @Mock
    private BinanceRepository repository;

    @InjectMocks
    private BinanceService service;

    @BeforeEach
     void setUp() {
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
    void getAveragePrice_throwsException() {
        OffsetDateTime from = OffsetDateTime.parse("2026-11-06T12:00:00Z");
        OffsetDateTime to = OffsetDateTime.parse("2026-11-05T12:00:00Z");

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.getAverage(from, to));

        verifyNoInteractions(repository);
    }

    @Test
    void getAll() {
        OffsetDateTime from = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1L), ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.ofInstant(Instant.ofEpochMilli(2L), ZoneOffset.UTC);

        BinanceResponseEntity binanceResponseEntity = new BinanceResponseEntity();
        binanceResponseEntity.setCloseTime(OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC));
        binanceResponseEntity.setPrice(BigDecimal.valueOf(10f));
        binanceResponseEntity.setFetchedAt(OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC));
        binanceResponseEntity.setSymbol(defaultSymbol);
        binanceResponseEntity.setId(1L);
        BinanceResponseEntity binanceResponseEntity1 = new BinanceResponseEntity();
        binanceResponseEntity1.setCloseTime(OffsetDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneOffset.UTC));
        binanceResponseEntity1.setPrice(BigDecimal.valueOf(20f));
        binanceResponseEntity1.setFetchedAt(OffsetDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneOffset.UTC));
        binanceResponseEntity1.setSymbol(defaultSymbol);
        binanceResponseEntity1.setId(2L);
        Pageable request = PageRequest.of(0,2);

        PageImpl<BinanceResponseEntity> page = new PageImpl<>(
                List.of(binanceResponseEntity, binanceResponseEntity1), request, 5
        );

        Mockito.when(repository.findAllBySymbolAndFetchedAtBetweenOrderByFetchedAt(defaultSymbol, from, to, request))
                .thenReturn(page);

        PageResponseDto<PriceResponseDto> result = service.getAll(from, to, request);

        assertThat(result.items()).hasSize(2);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.totalElements()).isEqualTo(5);
        assertThat(result.hasNext()).isTrue();

        Assertions.assertEquals(binanceResponseEntity.getFetchedAt(),
                result.items().get(0).fetchedAt());

    }

    @Test
    void getTrend_upward() {
        OffsetDateTime from = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1L), ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.ofInstant(Instant.ofEpochMilli(2L), ZoneOffset.UTC);

        BinanceResponseEntity binanceResponseEntity = new BinanceResponseEntity();
        binanceResponseEntity.setCloseTime(OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC));
        binanceResponseEntity.setPrice(BigDecimal.valueOf(10f));
        binanceResponseEntity.setFetchedAt(OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC));
        binanceResponseEntity.setSymbol(defaultSymbol);
        binanceResponseEntity.setId(1L);
        BinanceResponseEntity binanceResponseEntity1 = new BinanceResponseEntity();
        binanceResponseEntity1.setCloseTime(OffsetDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneOffset.UTC));
        binanceResponseEntity1.setPrice(BigDecimal.valueOf(20f));
        binanceResponseEntity1.setFetchedAt(OffsetDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneOffset.UTC));
        binanceResponseEntity1.setSymbol(defaultSymbol);
        binanceResponseEntity1.setId(2L);

        Mockito.when(repository.findFirstBySymbolAndFetchedAtBetweenOrderByFetchedAtAsc(defaultSymbol, from, to))
                .thenReturn(Optional.of(binanceResponseEntity));
        Mockito.when(repository.findFirstBySymbolAndFetchedAtBetweenOrderByFetchedAtDesc(defaultSymbol, from, to))
                .thenReturn(Optional.of(binanceResponseEntity1));

        TrendResponseDto trend = service.getTrend(from, to);

        assertThat(trend.trend()).isEqualTo(TrendType.UPWARD);

    }

    @Test
    void getTrend_downward() {
        OffsetDateTime from = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1L), ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.ofInstant(Instant.ofEpochMilli(2L), ZoneOffset.UTC);

        BinanceResponseEntity binanceResponseEntity = new BinanceResponseEntity();
        binanceResponseEntity.setCloseTime(OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC));
        binanceResponseEntity.setPrice(BigDecimal.valueOf(20f));
        binanceResponseEntity.setFetchedAt(OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC));
        binanceResponseEntity.setSymbol(defaultSymbol);
        binanceResponseEntity.setId(1L);
        BinanceResponseEntity binanceResponseEntity1 = new BinanceResponseEntity();
        binanceResponseEntity1.setCloseTime(OffsetDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneOffset.UTC));
        binanceResponseEntity1.setPrice(BigDecimal.valueOf(10f));
        binanceResponseEntity1.setFetchedAt(OffsetDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneOffset.UTC));
        binanceResponseEntity1.setSymbol(defaultSymbol);
        binanceResponseEntity1.setId(2L);

        Mockito.when(repository.findFirstBySymbolAndFetchedAtBetweenOrderByFetchedAtAsc(defaultSymbol, from, to))
                .thenReturn(Optional.of(binanceResponseEntity));
        Mockito.when(repository.findFirstBySymbolAndFetchedAtBetweenOrderByFetchedAtDesc(defaultSymbol, from, to))
                .thenReturn(Optional.of(binanceResponseEntity1));

        TrendResponseDto trend = service.getTrend(from, to);

        assertThat(trend.trend()).isEqualTo(TrendType.DOWNWARD);

    }

    @Test
    void getTrend_flat() {
        OffsetDateTime from = OffsetDateTime.ofInstant(Instant.ofEpochMilli(1L), ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.ofInstant(Instant.ofEpochMilli(2L), ZoneOffset.UTC);

        BinanceResponseEntity binanceResponseEntity = new BinanceResponseEntity();
        binanceResponseEntity.setCloseTime(OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC));
        binanceResponseEntity.setPrice(BigDecimal.valueOf(10f));
        binanceResponseEntity.setFetchedAt(OffsetDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneOffset.UTC));
        binanceResponseEntity.setSymbol(defaultSymbol);
        binanceResponseEntity.setId(1L);
        BinanceResponseEntity binanceResponseEntity1 = new BinanceResponseEntity();
        binanceResponseEntity1.setCloseTime(OffsetDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneOffset.UTC));
        binanceResponseEntity1.setPrice(BigDecimal.valueOf(10f));
        binanceResponseEntity1.setFetchedAt(OffsetDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneOffset.UTC));
        binanceResponseEntity1.setSymbol(defaultSymbol);
        binanceResponseEntity1.setId(2L);

        Mockito.when(repository.findFirstBySymbolAndFetchedAtBetweenOrderByFetchedAtAsc(defaultSymbol, from, to))
                .thenReturn(Optional.of(binanceResponseEntity));
        Mockito.when(repository.findFirstBySymbolAndFetchedAtBetweenOrderByFetchedAtDesc(defaultSymbol, from, to))
                .thenReturn(Optional.of(binanceResponseEntity1));

        TrendResponseDto trend = service.getTrend(from, to);

        assertThat(trend.trend()).isEqualTo(TrendType.FLAT);

    }
}