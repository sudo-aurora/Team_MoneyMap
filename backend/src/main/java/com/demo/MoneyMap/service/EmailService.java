package com.demo.MoneyMap.service;

import java.math.BigDecimal;

public interface EmailService {

    void sendAssetDropAlert(
            String to,
            String portfolioName,
            String symbol,
            BigDecimal currentPrice,
            Double dropPercent
    );

    void sendLowValueAlert(String mail, String testPortfolio, BigDecimal bigDecimal);
}

