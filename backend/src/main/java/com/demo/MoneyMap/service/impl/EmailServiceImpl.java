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
    public void sendLowValueAlert(String to, String portfolioName, BigDecimal value) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("⚠ Portfolio Value Alert");
        message.setText("""
                Your portfolio "%s" has a low total value.

                Current Value: %s

                Please review your investments.
                """.formatted(portfolioName, value));

        mailSender.send(message);
        log.info("Low value alert sent to {}", to);
    }
}
