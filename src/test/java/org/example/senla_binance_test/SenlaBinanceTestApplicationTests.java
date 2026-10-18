package org.example.senla_binance_test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SenlaBinanceTestApplicationTests {

    @Test
    void contextLoads() {
    }

}
