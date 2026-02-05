package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.entity.AssetPriceHistory;
import com.demo.MoneyMap.repository.AssetPriceHistoryRepository;
import com.demo.MoneyMap.service.AssetPriceHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssetPriceHistoryServiceImpl implements AssetPriceHistoryService {

    private final AssetPriceHistoryRepository repository;
    private final RestTemplate restTemplate;

    @Value("${market.data.base-url}")
    private String flaskBaseUrl;

    @Override
    public void fetchAndStoreHistory(String symbol, String period) {

        String url = flaskBaseUrl + "/stock/" + symbol + "?period=" + period;

        Map response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> history =
                (List<Map<String, Object>>) response.get("historicalData");

        for (Map<String, Object> row : history) {
            LocalDate date = LocalDate.parse((String) row.get("date"));

            if (repository.existsBySymbolAndPriceDate(symbol, date)) {
                continue;
            }

            AssetPriceHistory entity = AssetPriceHistory.builder()
                    .symbol(symbol.toUpperCase())
                    .priceDate(date)
                    .openPrice(BigDecimal.valueOf((Double) row.get("open")))
                    .highPrice(BigDecimal.valueOf((Double) row.get("high")))
                    .lowPrice(BigDecimal.valueOf((Double) row.get("low")))
                    .closePrice(BigDecimal.valueOf((Double) row.get("close")))
                    .volume(((Number) row.get("volume")).longValue())
                    .build();

            repository.save(entity);
        }

        log.info("Stored historical data for {}", symbol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetPriceHistory> getHistory(String symbol) {
        return repository.findBySymbolOrderByPriceDateAsc(symbol.toUpperCase());
    }
}
