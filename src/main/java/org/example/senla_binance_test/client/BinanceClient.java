package org.example.senla_binance_test.client;

import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class BinanceClient {

    private final RestClient client;

    public BinanceClient() {
        this.client = RestClient.builder()
                .baseUrl("https://api.binance.com")
                .build();
    }


    public BinancePriceResponse getAveragePrice(String symbol) {

        BinancePriceResponse response = client.get()
                .uri("/api/v3/avgPrice?symbol={symbol}", symbol)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, res) -> {
                    throw new RestClientResponseException("Ошибка от бинанса",
                            res.getStatusCode().value(),
                            res.getStatusText(),
                            res.getHeaders(),
                            res.getBody().readAllBytes(),
                            null
                    );
                })
                .body(BinancePriceResponse.class);

        if (response == null || response.price() == null) {
            throw new RuntimeException("Не удалось получить цену от Binance");
        }

        return response;
    }
}
