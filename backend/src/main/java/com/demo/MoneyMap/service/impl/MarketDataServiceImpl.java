package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.dto.response.FinnhubQuoteResponse;
import com.demo.MoneyMap.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class MarketDataServiceImpl implements MarketDataService {

    private final WebClient webClient = WebClient.create("http://172.30.1.148:5000");

    public FinnhubQuoteResponse getLiveQuote(String tickerId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", tickerId)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubQuoteResponse.class)
                .block(); // blocking is OK for now
    }
}
