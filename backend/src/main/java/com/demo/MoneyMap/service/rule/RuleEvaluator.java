package com.demo.MoneyMap.service.rule;

import com.demo.MoneyMap.entity.Alert;
import com.demo.MoneyMap.entity.MonitoringRule;
import com.demo.MoneyMap.entity.Payment;

import java.util.Optional;

/**
 * Strategy interface for rule evaluation.
 * Each rule type implements this interface.
 */
public interface RuleEvaluator {

    /**
     * Evaluate a payment against a rule.
     * Returns an Alert if the rule is triggered, empty otherwise.
     */
    Optional<Alert> evaluate(Payment payment, MonitoringRule rule);

    /**
     * Check if this evaluator supports the given rule.
     */
    boolean supports(MonitoringRule rule);
}
