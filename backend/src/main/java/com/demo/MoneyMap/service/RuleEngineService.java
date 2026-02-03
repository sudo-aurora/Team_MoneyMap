package com.demo.MoneyMap.service;

import com.demo.MoneyMap.entity.Alert;
import com.demo.MoneyMap.entity.Payment;

import java.util.List;

/**
 * Service interface for the rule engine.
 * Evaluates payments against monitoring rules.
 */
public interface RuleEngineService {

    /**
     * Evaluate a payment against all active rules.
     * Returns list of alerts generated.
     */
    List<Alert> evaluatePayment(Payment payment);

    /**
     * Evaluate a specific rule against a payment.
     */
    Alert evaluateRule(Long ruleId, Payment payment);

    /**
     * Re-evaluate all recent payments against a specific rule.
     * Useful when a new rule is created.
     */
    List<Alert> reEvaluateRecentPayments(Long ruleId, int hoursBack);
}
