package org.example.senla_binance_test.controller;

import lombok.RequiredArgsConstructor;
import org.example.senla_binance_test.dto.AveragePriceResponseDto;
import org.example.senla_binance_test.dto.PageResponseDto;
import org.example.senla_binance_test.dto.PriceResponseDto;
import org.example.senla_binance_test.dto.TrendResponseDto;
import org.example.senla_binance_test.service.BinanceService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public PageResponseDto<PriceResponseDto> getAll(
            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @PageableDefault Pageable pageable
    ) {
        return service.getAll(from, to, pageable);
    }

    @GetMapping("/trend")
    public TrendResponseDto getTrend(
            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        return service.getTrend(from, to);
    }
}
