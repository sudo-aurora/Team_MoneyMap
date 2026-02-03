package com.demo.MoneyMap.service.rule;

import com.demo.MoneyMap.entity.Alert;
import com.demo.MoneyMap.entity.MonitoringRule;
import com.demo.MoneyMap.entity.Payment;
import com.demo.MoneyMap.entity.enums.RuleType;
import com.demo.MoneyMap.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Rule evaluator for VELOCITY rule type.
 * Triggers alert when N transactions occur within T time period from same account.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VelocityRuleEvaluator implements RuleEvaluator {

    private final PaymentRepository paymentRepository;

    @Override
    public Optional<Alert> evaluate(Payment payment, MonitoringRule rule) {
        if (!supports(rule)) {
            return Optional.empty();
        }

        Integer maxTransactions = rule.getMaxTransactions();
        Integer timeWindowMinutes = rule.getTimeWindowMinutes();

        if (maxTransactions == null || timeWindowMinutes == null) {
            log.warn("Velocity rule {} has incomplete configuration", rule.getId());
            return Optional.empty();
        }

        LocalDateTime since = LocalDateTime.now().minusMinutes(timeWindowMinutes);
        long recentCount = paymentRepository.countRecentBySourceAccount(payment.getSourceAccount(), since);

        if (recentCount >= maxTransactions) {
            log.info("Velocity rule triggered for account {}: {} transactions in {} minutes", 
                    payment.getSourceAccount(), recentCount, timeWindowMinutes);

            Alert alert = Alert.builder()
                    .rule(rule)
                    .severity(rule.getSeverity())
                    .message(String.format("High transaction velocity detected: %d transactions from account %s in %d minutes (threshold: %d)",
                            recentCount, payment.getSourceAccount(), timeWindowMinutes, maxTransactions))
                    .accountId(payment.getSourceAccount())
                    .build();
            alert.addTriggeringPayment(payment);

            return Optional.of(alert);
        }

        return Optional.empty();
    }

    @Override
    public boolean supports(MonitoringRule rule) {
        return rule.getRuleType() == RuleType.VELOCITY;
    }
}
