package org.example.senla_binance_test.client;

import lombok.extern.slf4j.Slf4j;
import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.exception.BinanceRateLimitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
public class BinanceClient {

    private final RestClient client;

    public BinanceClient(@Value("${binance.api.base-url:https://api.binance.com/}") String baseUrl) {
        client = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public BinancePriceResponse getAveragePrice(String symbol) {
        BinancePriceResponse response = client.get()
                .uri("/api/v3/avgPrice?symbol={symbol}", symbol)
                .retrieve()
                .onStatus(status -> status.value() == 429 || status.value() == 418, (req, res) -> {
                    String retryAfterHeader = res.getHeaders().getFirst("Retry-After");
                    long retryAfter = parseRetryAfter(retryAfterHeader);
                    throw new BinanceRateLimitException(
                            "Достигнут лимит запросов Binance (HTTP " + res.getStatusCode().value() + ")",
                            retryAfter
                    );
                })
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new RestClientResponseException(
                            "Ошибка при обращении к Binance API: " + res.getStatusText(),
                            res.getStatusCode().value(),
                            res.getStatusText(),
                            res.getHeaders(),
                            res.getBody().readAllBytes(),
                            null
                    );
                })
                .body(BinancePriceResponse.class);

        if (response == null || response.price() == null || response.closeTime() == null) {
            throw new IllegalStateException("Получен пустой или некорректный ответ от Binance API");
        }

        return response;
    }

    private long parseRetryAfter(String header) {
        if (header == null || header.isBlank()) {
            return 60L;
        }
        try {
            return Long.parseLong(header.trim());
        } catch (NumberFormatException e) {
            return 60L;
        }
    }
}