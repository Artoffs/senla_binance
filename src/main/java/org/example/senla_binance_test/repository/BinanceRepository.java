package org.example.senla_binance_test.repository;

import org.example.senla_binance_test.model.BinanceResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface BinanceRepository extends JpaRepository<BinanceResponseEntity, Long> {

    boolean existsBySymbolAndCloseTime(String symbol, OffsetDateTime closeTime);
    Optional<BinanceResponseEntity> findFirstByOrderByFetchedAtDesc();
}
