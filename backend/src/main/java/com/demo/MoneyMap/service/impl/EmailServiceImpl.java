package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${moneymap.email.test-recipient:sargampuram.cloud@gmail.com}")
    private String testRecipient;

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

    @Override
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true for HTML content
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendTestEmail() {
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String testContent = """
                <html>
                <body style='font-family: Arial, sans-serif;'>
                <h2>MoneyMap Email Test</h2>
                <p>This is a test email to verify that the email configuration is working correctly.</p>
                <p>If you received this email, the email service is functioning properly.</p>
                <p>Timestamp: """ + timestamp + """
                <hr>
                <p><em>MoneyMap Portfolio Management System</em></p>
                </body>
                </html>
                """;
        
        sendEmail(testRecipient, "MoneyMap Email Test", testContent);
        log.info("Test email sent successfully");
    }

}
