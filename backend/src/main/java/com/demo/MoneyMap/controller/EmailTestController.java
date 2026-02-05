package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @GetMapping("/email")
    public String testEmail() {

        emailService.sendLowValueAlert(
                "siddsubramanian2210@gmail.com",
                "Test Portfolio",
                new BigDecimal("12345")
        );

        return "Email sent successfully";
    }
}
