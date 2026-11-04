package org.example.senla_binance_test.controller;

import lombok.RequiredArgsConstructor;
import org.example.senla_binance_test.dto.AveragePriceResponseDto;
import org.example.senla_binance_test.dto.PriceResponseDto;
import org.example.senla_binance_test.service.BinanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/prices")
public class BinanceController {

    private final BinanceService service;

    @GetMapping("/latest")
    public PriceResponseDto getLatest() {
        return service.getLatest();
    }

    @GetMapping("/average")
    public AveragePriceResponseDto getAverage(
            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
            ) {
        return service.getAverage(from, to);
    }
}
