package com.demo.MoneyMap.service.rule;

import com.demo.MoneyMap.entity.Alert;
import com.demo.MoneyMap.entity.MonitoringRule;
import com.demo.MoneyMap.entity.Payment;
import com.demo.MoneyMap.entity.enums.RuleType;
import com.demo.MoneyMap.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Rule evaluator for DAILY_LIMIT rule type.
 * Triggers alert when cumulative transaction amount exceeds daily limit.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DailyLimitRuleEvaluator implements RuleEvaluator {

    private final PaymentRepository paymentRepository;

    @Override
    public Optional<Alert> evaluate(Payment payment, MonitoringRule rule) {
        if (!supports(rule)) {
            return Optional.empty();
        }

        BigDecimal dailyLimit = rule.getDailyLimitAmount();
        if (dailyLimit == null) {
            log.warn("Daily limit rule {} has no limit configured", rule.getId());
            return Optional.empty();
        }

        // Calculate day boundaries
        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.atTime(LocalTime.MAX);

        // Get total amount for the day (including current payment)
        BigDecimal dailyTotal = paymentRepository.sumAmountByAccountAndDate(
                payment.getSourceAccount(), dayStart, dayEnd);
        
        // Add current payment amount
        dailyTotal = dailyTotal.add(payment.getAmount());

        if (dailyTotal.compareTo(dailyLimit) > 0) {
            log.info("Daily limit rule triggered for account {}: daily total {} exceeds limit {}",
                    payment.getSourceAccount(), dailyTotal, dailyLimit);

            Alert alert = Alert.builder()
                    .rule(rule)
                    .severity(rule.getSeverity())
                    .message(String.format("Daily transaction limit exceeded: Account %s has transacted %.2f %s today (limit: %.2f)",
                            payment.getSourceAccount(), dailyTotal, payment.getCurrency(), dailyLimit))
                    .accountId(payment.getSourceAccount())
                    .build();
            alert.addTriggeringPayment(payment);

            return Optional.of(alert);
        }

        return Optional.empty();
    }

    @Override
    public boolean supports(MonitoringRule rule) {
        return rule.getRuleType() == RuleType.DAILY_LIMIT;
    }
}
