package org.example.senla_binance_test.repository;

import org.example.senla_binance_test.model.BinanceResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface BinanceRepository extends JpaRepository<BinanceResponseEntity, Long> {

    boolean existsBySymbolAndCloseTime(String symbol, OffsetDateTime closeTime);
    Optional<BinanceResponseEntity> findFirstBySymbolOrderByFetchedAtDesc(String symbol);


    @Query("""
    SELECT avg(resp.price) from BinanceResponseEntity resp
    WHERE resp.symbol = :symbol
    AND resp.fetchedAt between :from and :to
""")
    Optional<BigDecimal> getAverageForPeriod(
            @Param("symbol") String symbol,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to);
}
