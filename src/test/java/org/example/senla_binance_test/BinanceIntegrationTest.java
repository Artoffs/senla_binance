package org.example.senla_binance_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.model.BinanceResponseEntity;
import org.example.senla_binance_test.repository.BinanceRepository;
import org.example.senla_binance_test.scheduler.PriceScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest(properties = "app.scheduling.enabled=false")
@AutoConfigureMockMvc
class BinanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    PriceScheduler scheduler;

    @Autowired
    BinanceRepository repository;

    @Container
    private static final PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16");

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("binance.api.base-url", wireMock::baseUrl);
    }

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void getLatest() throws Exception {

        BinancePriceResponse resp = new BinancePriceResponse("1", BigDecimal.valueOf(10L), 123L);

        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/v3/avgPrice?symbol=BNBUSDT"))
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(resp))));

        scheduler.fetchPrice();

        List<BinanceResponseEntity> all = repository.findAll();

        assertThat(all).hasSize(1);
        assertThat(all.get(0).getPrice()).isEqualByComparingTo("10");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("BNBUSDT"))
                .andExpect(jsonPath("$.price").value(10.0))
                .andExpect(jsonPath("$.fetchedAt").isNotEmpty());
    }

    @Test
    void getAverage() throws Exception {
        BinancePriceResponse resp = new BinancePriceResponse("1", BigDecimal.valueOf(10L), 123L);
        BinancePriceResponse resp1 = new BinancePriceResponse("2", BigDecimal.valueOf(20L), 124L);

        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/v3/avgPrice?symbol=BNBUSDT"))
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(resp))));

        scheduler.fetchPrice();

        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/v3/avgPrice?symbol=BNBUSDT"))
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(resp1))));

        scheduler.fetchPrice();

        List<BinanceResponseEntity> all = repository.findAll();

        assertThat(all).hasSize(2);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        String from = now.minusHours(1).toString();
        String to = now.plusHours(1).toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/average")
                        .param("from", from)
                        .param("to", to))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averagePrice").value(15));
    }

    @Test
    void getTrend() throws Exception {
        BinancePriceResponse resp = new BinancePriceResponse("1", BigDecimal.valueOf(10L), 123L);
        BinancePriceResponse resp1 = new BinancePriceResponse("2", BigDecimal.valueOf(20L), 124L);

        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/v3/avgPrice?symbol=BNBUSDT"))
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(resp))));

        scheduler.fetchPrice();

        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/v3/avgPrice?symbol=BNBUSDT"))
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(resp1))));

        scheduler.fetchPrice();

        List<BinanceResponseEntity> all = repository.findAll();

        assertThat(all).hasSize(2);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        String from = now.minusHours(1).toString();
        String to = now.plusHours(1).toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/trend")
                        .param("from", from)
                        .param("to", to))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trend").value("UPWARD"));
    }

    @Test
    void getPrices() throws Exception {
        for (int i = 1; i <= 3; i++) {
            BinancePriceResponse response = new BinancePriceResponse(
                    "5",
                    BigDecimal.valueOf(i * 10L),
                    1L + i
            );

            wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/v3/avgPrice?symbol=BNBUSDT"))
                    .willReturn(WireMock.okJson(objectMapper.writeValueAsString(response))));

            scheduler.fetchPrice();
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        String from = now.minusHours(1).toString();
        String to = now.plusHours(1).toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices")
                        .param("from", from)
                        .param("to", to)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].price").value(10.0))
                .andExpect(jsonPath("$.items[1].price").value(20.0));


    }
}
