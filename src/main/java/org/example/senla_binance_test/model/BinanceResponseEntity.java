package org.example.senla_binance_test.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "binance_history")
@Getter
@Setter
public class BinanceResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "symbol", nullable = false)
    private String symbol;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "close_time", nullable = false)
    private OffsetDateTime closeTime;
    @Column(name = "fetched_at", nullable = false)
    private OffsetDateTime fetchedAt;
}
