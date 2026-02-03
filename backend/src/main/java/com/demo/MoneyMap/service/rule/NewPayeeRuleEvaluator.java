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
 * Rule evaluator for NEW_PAYEE rule type.
 * Triggers alert when a transaction is made to a previously unseen payee.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NewPayeeRuleEvaluator implements RuleEvaluator {

    private final PaymentRepository paymentRepository;

    @Override
    public Optional<Alert> evaluate(Payment payment, MonitoringRule rule) {
        if (!supports(rule)) {
            return Optional.empty();
        }

        // Check if this payee has been used before by this account
        LocalDateTime cutoffDate = payment.getCreatedAt() != null 
                ? payment.getCreatedAt() 
                : LocalDateTime.now();

        long previousCount = paymentRepository.countPreviousPaymentToPayee(
                payment.getSourceAccount(),
                payment.getDestinationAccount(),
                cutoffDate
        );

        if (previousCount == 0) {
            log.info("New payee rule triggered for payment {}: first transaction from {} to {}",
                    payment.getId(), payment.getSourceAccount(), payment.getDestinationAccount());

            Alert alert = Alert.builder()
                    .rule(rule)
                    .severity(rule.getSeverity())
                    .message(String.format("First transaction to new payee: Account %s sending to %s (amount: %.2f %s)",
                            payment.getSourceAccount(), payment.getDestinationAccount(),
                            payment.getAmount(), payment.getCurrency()))
                    .accountId(payment.getSourceAccount())
                    .build();
            alert.addTriggeringPayment(payment);

            return Optional.of(alert);
        }

        return Optional.empty();
    }

    @Override
    public boolean supports(MonitoringRule rule) {
        return rule.getRuleType() == RuleType.NEW_PAYEE;
    }
}
