package org.example.senla_binance_test.service;

import lombok.RequiredArgsConstructor;
import org.example.senla_binance_test.dto.BinancePriceResponse;
import org.example.senla_binance_test.mapper.BinanceMapper;
import org.example.senla_binance_test.model.BinanceResponseEntity;
import org.example.senla_binance_test.repository.BinanceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinanceService {

    private final BinanceRepository repository;

    public BinanceResponseEntity save(String symbol, BinancePriceResponse response) {
        return repository.save(BinanceMapper.toEntity(response, symbol));
    }

    public BinanceResponseEntity getLatest() {
        return repository.findFirstByOrderByFetchedAtDesc().get();
    }
}
