package com.demo.MoneyMap.service;

import java.math.BigDecimal;

public interface EmailService {
    void sendLowValueAlert(String to, String portfolioName, BigDecimal value);
}
