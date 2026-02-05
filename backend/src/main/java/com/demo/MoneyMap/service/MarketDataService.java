package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.response.FinnhubQuoteResponse;

public interface MarketDataService {
    public FinnhubQuoteResponse getLiveQuote(String tickerId);
}
