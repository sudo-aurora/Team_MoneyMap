package com.demo.MoneyMap.service;

import com.demo.MoneyMap.entity.AssetPriceHistory;

import java.util.List;

public interface AssetPriceHistoryService {

    void fetchAndStoreHistory(String symbol, String period);

    List<AssetPriceHistory> getHistory(String symbol);
}
