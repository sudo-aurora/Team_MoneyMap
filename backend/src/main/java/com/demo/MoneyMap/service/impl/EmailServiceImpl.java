package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendAssetDropAlert(
            String to,
            String portfolioName,
            String symbol,
            BigDecimal currentPrice,
            Double dropPercent
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("⚠ Asset Price Alert: " + symbol);
        message.setText("""
                Alert for your portfolio: %s

                Asset: %s
                Current Price: %s
                Drop Today: %.2f%%

                Please review your holdings.
                """.formatted(
                portfolioName,
                symbol,
                currentPrice,
                dropPercent
        ));

        mailSender.send(message);
        log.info("Price drop alert sent for {} to {}", symbol, to);
    }
    @Override
    public void sendLowValueAlert(
            String to,
            String portfolioName,
            BigDecimal totalValue
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("⚠ Portfolio Value Alert");

        message.setText("""
            Alert for your portfolio: %s

            Your portfolio value has dropped significantly.

            Current Total Value: %s

            Please review your investments.
            """.formatted(
                portfolioName,
                totalValue
        ));

        mailSender.send(message);

        log.info("Low value portfolio alert sent to {} for portfolio {}", to, portfolioName);
    }

}
