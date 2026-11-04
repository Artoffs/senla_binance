package org.example.senla_binance_test.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.senla_binance_test.client.BinanceClient;
import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.exception.BinanceRateLimitException;
import org.example.senla_binance_test.service.BinanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceScheduler {

    private final BinanceClient binanceClient;
    private final BinanceService binanceService;

    private Instant timeout = Instant.MIN;

    @Scheduled(fixedRateString = "${BINANCE_POLL_INTERVAL:PT30S}")
    public void fetchPrice() {

        if(Instant.now().isBefore(timeout)) {
            log.warn("Пропуск запроса из-за рейт лимитера");
            return;
        }

        try {
            log.info("Запрос средней цены");
            BinancePriceResponse price = binanceClient.getAveragePrice("BNBUSDT");
            log.info("Получена цена: {}", price);
            binanceService.saveIfNotExists("BNBUSDT", price);
        } catch (BinanceRateLimitException e) {
            timeout = Instant.now().plusSeconds(e.getRetryAfterSeconds());
            log.error("Опрос будет приостановлен на {} секунд", e.getRetryAfterSeconds());
        }
    }
}