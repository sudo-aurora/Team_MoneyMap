package com.demo.MoneyMap.repository;

import com.demo.MoneyMap.entity.MonitoringRule;
import com.demo.MoneyMap.entity.enums.AlertSeverity;
import com.demo.MoneyMap.entity.enums.RuleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for MonitoringRule entity operations.
 */
@Repository
public interface MonitoringRuleRepository extends JpaRepository<MonitoringRule, Long> {

    /**
     * Find all active rules.
     */
    List<MonitoringRule> findByActiveTrue();

    /**
     * Find active rules by type.
     */
    List<MonitoringRule> findByRuleTypeAndActiveTrue(RuleType ruleType);

    /**
     * Find rules by type.
     */
    List<MonitoringRule> findByRuleType(RuleType ruleType);

    /**
     * Find rules by type with pagination.
     */
    Page<MonitoringRule> findByRuleType(RuleType ruleType, Pageable pageable);

    /**
     * Find rules by severity.
     */
    List<MonitoringRule> findBySeverity(AlertSeverity severity);

    /**
     * Find rules by severity with pagination.
     */
    Page<MonitoringRule> findBySeverity(AlertSeverity severity, Pageable pageable);

    /**
     * Find active rules with pagination.
     */
    Page<MonitoringRule> findByActiveTrue(Pageable pageable);

    /**
     * Check if rule name exists.
     */
    boolean existsByRuleName(String ruleName);

    /**
     * Check if rule name exists excluding a specific rule.
     */
    boolean existsByRuleNameAndIdNot(String ruleName, Long id);

    /**
     * Count active rules.
     */
    long countByActiveTrue();
}
