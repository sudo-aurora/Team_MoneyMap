package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.dto.response.FinnhubQuoteResponse;
import com.demo.MoneyMap.service.MarketQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class MarketQuoteServiceImpl implements MarketQuoteService {

    private final WebClient webClient =
            WebClient.create("http://172.30.1.148:5000");

    @Override
    public FinnhubQuoteResponse getQuote(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubQuoteResponse.class)
                .block();
    }
}
