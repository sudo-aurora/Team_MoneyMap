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
    
    /**
     * Send quarterly report email to client.
     * @param to Client email address
     * @param subject Email subject
     * @param content HTML email content
     */
    void sendEmail(String to, String subject, String content);
    
    /**
     * Send test email to verify email configuration.
     */
    void sendTestEmail();
}

