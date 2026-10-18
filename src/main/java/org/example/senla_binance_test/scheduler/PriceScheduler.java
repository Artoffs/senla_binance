package org.example.senla_binance_test.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.senla_binance_test.client.BinanceClient;
import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.service.BinanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceScheduler {

    private final BinanceClient binanceClient;
    private final BinanceService binanceService;

    @Scheduled(fixedRateString = "${BINANCE_POLL_INTERVAL:PT30S}")
    public void fetchPrice() {
        try {
            log.info("Пытаюсь получить цену...");
            BinancePriceResponse price = binanceClient.getAveragePrice("BNBUSDT");
            log.info("Получена цена: {}", price);
            binanceService.save("BNBUSDT", price);
            log.info("Успешно сохранено в БД");
        } catch (Exception e) {
            log.error("ОШИБКА в шедулере: ", e); // <-- теперь ты увидишь стектрейс
        }
    }
}