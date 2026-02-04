package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.dto.response.FinnhubQuoteResponse;
import com.demo.MoneyMap.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataService marketDataService;

    // Example: GET /api/market/price/AAPL
    @GetMapping("/price/{tickerId}")
    public FinnhubQuoteResponse getCurrentPrice(
            @PathVariable String tickerId) {

        return marketDataService.getLiveQuote(tickerId);
    }
}
