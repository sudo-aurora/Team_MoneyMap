package com.demo.MoneyMap.service.rule;

import com.demo.MoneyMap.entity.Alert;
import com.demo.MoneyMap.entity.MonitoringRule;
import com.demo.MoneyMap.entity.Payment;
import com.demo.MoneyMap.entity.enums.RuleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Rule evaluator for AMOUNT_THRESHOLD rule type.
 * Triggers alert when transaction amount exceeds configured threshold.
 */
@Component
@Slf4j
public class AmountThresholdRuleEvaluator implements RuleEvaluator {

    @Override
    public Optional<Alert> evaluate(Payment payment, MonitoringRule rule) {
        if (!supports(rule)) {
            return Optional.empty();
        }

        BigDecimal threshold = rule.getThresholdAmount();
        if (threshold == null) {
            log.warn("Amount threshold rule {} has no threshold configured", rule.getId());
            return Optional.empty();
        }

        // Check currency match if specified
        if (rule.getThresholdCurrency() != null && 
            !rule.getThresholdCurrency().equalsIgnoreCase(payment.getCurrency())) {
            return Optional.empty();
        }

        if (payment.getAmount().compareTo(threshold) > 0) {
            log.info("Amount threshold rule triggered for payment {}: {} > {}", 
                    payment.getId(), payment.getAmount(), threshold);

            Alert alert = Alert.builder()
                    .rule(rule)
                    .severity(rule.getSeverity())
                    .message(String.format("Transaction amount %.2f %s exceeds threshold of %.2f %s",
                            payment.getAmount(), payment.getCurrency(),
                            threshold, rule.getThresholdCurrency() != null ? rule.getThresholdCurrency() : payment.getCurrency()))
                    .accountId(payment.getSourceAccount())
                    .build();
            alert.addTriggeringPayment(payment);

            return Optional.of(alert);
        }

        return Optional.empty();
    }

    @Override
    public boolean supports(MonitoringRule rule) {
        return rule.getRuleType() == RuleType.AMOUNT_THRESHOLD;
    }
}
