package com.demo.MoneyMap.repository;

import com.demo.MoneyMap.entity.AssetPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AssetPriceHistoryRepository
        extends JpaRepository<AssetPriceHistory, Long> {

    List<AssetPriceHistory> findBySymbolOrderByPriceDateAsc(String symbol);

    List<AssetPriceHistory> findBySymbolAndPriceDateBetweenOrderByPriceDateAsc(
            String symbol,
            LocalDate start,
            LocalDate end
    );

    boolean existsBySymbolAndPriceDate(String symbol, LocalDate priceDate);
}
