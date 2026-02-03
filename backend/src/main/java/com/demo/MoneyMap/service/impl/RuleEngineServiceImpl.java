package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.entity.Alert;
import com.demo.MoneyMap.entity.MonitoringRule;
import com.demo.MoneyMap.entity.Payment;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.repository.AlertRepository;
import com.demo.MoneyMap.repository.MonitoringRuleRepository;
import com.demo.MoneyMap.repository.PaymentRepository;
import com.demo.MoneyMap.service.RuleEngineService;
import com.demo.MoneyMap.service.rule.RuleEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of RuleEngineService.
 * Uses Strategy pattern to evaluate different rule types.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RuleEngineServiceImpl implements RuleEngineService {

    private final MonitoringRuleRepository ruleRepository;
    private final AlertRepository alertRepository;
    private final PaymentRepository paymentRepository;
    private final List<RuleEvaluator> ruleEvaluators;

    @Override
    public List<Alert> evaluatePayment(Payment payment) {
        log.debug("Evaluating payment {} against all active rules", payment.getId());

        List<MonitoringRule> activeRules = ruleRepository.findByActiveTrue();
        List<Alert> generatedAlerts = new ArrayList<>();

        for (MonitoringRule rule : activeRules) {
            try {
                Optional<Alert> alert = evaluatePaymentAgainstRule(payment, rule);
                if (alert.isPresent()) {
                    Alert savedAlert = alertRepository.save(alert.get());
                    generatedAlerts.add(savedAlert);
                    log.info("Alert {} generated for payment {} by rule {}", 
                            savedAlert.getId(), payment.getId(), rule.getRuleName());
                }
            } catch (Exception e) {
                log.error("Error evaluating rule {} for payment {}: {}", 
                        rule.getId(), payment.getId(), e.getMessage());
            }
        }

        log.info("Payment {} evaluation complete: {} alerts generated", 
                payment.getId(), generatedAlerts.size());
        return generatedAlerts;
    }

    @Override
    public Alert evaluateRule(Long ruleId, Payment payment) {
        MonitoringRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found with ID: " + ruleId));

        Optional<Alert> alert = evaluatePaymentAgainstRule(payment, rule);
        if (alert.isPresent()) {
            return alertRepository.save(alert.get());
        }
        return null;
    }

    @Override
    public List<Alert> reEvaluateRecentPayments(Long ruleId, int hoursBack) {
        MonitoringRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found with ID: " + ruleId));

        LocalDateTime since = LocalDateTime.now().minusHours(hoursBack);
        List<Payment> recentPayments = paymentRepository.findByAmountGreaterThanAndCreatedAfter(
                java.math.BigDecimal.ZERO, since);

        List<Alert> generatedAlerts = new ArrayList<>();
        for (Payment payment : recentPayments) {
            Optional<Alert> alert = evaluatePaymentAgainstRule(payment, rule);
            if (alert.isPresent()) {
                Alert savedAlert = alertRepository.save(alert.get());
                generatedAlerts.add(savedAlert);
            }
        }

        log.info("Re-evaluated {} payments against rule {}: {} alerts generated",
                recentPayments.size(), ruleId, generatedAlerts.size());
        return generatedAlerts;
    }

    /**
     * Find the appropriate evaluator and evaluate the payment.
     */
    private Optional<Alert> evaluatePaymentAgainstRule(Payment payment, MonitoringRule rule) {
        for (RuleEvaluator evaluator : ruleEvaluators) {
            if (evaluator.supports(rule)) {
                return evaluator.evaluate(payment, rule);
            }
        }
        log.warn("No evaluator found for rule type: {}", rule.getRuleType());
        return Optional.empty();
    }
}
