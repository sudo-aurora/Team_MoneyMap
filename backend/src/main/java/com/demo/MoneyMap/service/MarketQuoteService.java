package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.response.FinnhubQuoteResponse;

public interface MarketQuoteService {
    FinnhubQuoteResponse getQuote(String symbol);
}

