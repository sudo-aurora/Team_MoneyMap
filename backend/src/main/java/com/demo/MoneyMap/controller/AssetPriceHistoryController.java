package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.entity.AssetPriceHistory;
import com.demo.MoneyMap.service.AssetPriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price-history")
@RequiredArgsConstructor
public class AssetPriceHistoryController {

    private final AssetPriceHistoryService historyService;

    // 1️⃣ Fetch from Flask & store
    @PostMapping("/fetch/{symbol}")
    public String fetchAndStore(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "3mo") String period
    ) {
        historyService.fetchAndStoreHistory(symbol, period);
        return "Historical data stored for " + symbol;
    }

    // 2️⃣ Get stored history
    @GetMapping("/{symbol}")
    public List<AssetPriceHistory> getHistory(@PathVariable String symbol) {
        return historyService.getHistory(symbol);
    }
}
