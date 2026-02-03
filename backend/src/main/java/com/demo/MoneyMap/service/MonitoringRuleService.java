package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.MonitoringRuleRequestDTO;
import com.demo.MoneyMap.dto.response.MonitoringRuleResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.enums.RuleType;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for monitoring rule management.
 */
public interface MonitoringRuleService {

    /**
     * Create a new monitoring rule.
     */
    MonitoringRuleResponseDTO createRule(MonitoringRuleRequestDTO requestDTO);

    /**
     * Get rule by ID.
     */
    MonitoringRuleResponseDTO getRuleById(Long id);

    /**
     * Get all rules with pagination.
     */
    PagedResponseDTO<MonitoringRuleResponseDTO> getAllRules(Pageable pageable);

    /**
     * Get active rules.
     */
    List<MonitoringRuleResponseDTO> getActiveRules();

    /**
     * Get rules by type.
     */
    PagedResponseDTO<MonitoringRuleResponseDTO> getRulesByType(RuleType ruleType, Pageable pageable);

    /**
     * Update a rule.
     */
    MonitoringRuleResponseDTO updateRule(Long id, MonitoringRuleRequestDTO requestDTO);

    /**
     * Activate a rule.
     */
    MonitoringRuleResponseDTO activateRule(Long id);

    /**
     * Deactivate a rule.
     */
    MonitoringRuleResponseDTO deactivateRule(Long id);

    /**
     * Delete a rule.
     */
    void deleteRule(Long id);

    /**
     * Get all rule types.
     */
    List<RuleType> getRuleTypes();
}
