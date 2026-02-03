package com.demo.MoneyMap.mapper;

import com.demo.MoneyMap.dto.request.MonitoringRuleRequestDTO;
import com.demo.MoneyMap.dto.response.MonitoringRuleResponseDTO;
import com.demo.MoneyMap.entity.MonitoringRule;
import org.springframework.stereotype.Component;

/**
 * Mapper class for MonitoringRule entity and DTOs.
 */
@Component
public class MonitoringRuleMapper {

    /**
     * Convert MonitoringRuleRequestDTO to MonitoringRule entity.
     */
    public MonitoringRule toEntity(MonitoringRuleRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return MonitoringRule.builder()
                .ruleName(dto.getRuleName())
                .ruleType(dto.getRuleType())
                .severity(dto.getSeverity())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .description(dto.getDescription())
                .thresholdAmount(dto.getThresholdAmount())
                .thresholdCurrency(dto.getThresholdCurrency())
                .maxTransactions(dto.getMaxTransactions())
                .timeWindowMinutes(dto.getTimeWindowMinutes())
                .dailyLimitAmount(dto.getDailyLimitAmount())
                .lookbackDays(dto.getLookbackDays())
                .build();
    }

    /**
     * Convert MonitoringRule entity to MonitoringRuleResponseDTO.
     */
    public MonitoringRuleResponseDTO toResponseDTO(MonitoringRule entity) {
        if (entity == null) {
            return null;
        }

        return MonitoringRuleResponseDTO.builder()
                .id(entity.getId())
                .ruleName(entity.getRuleName())
                .ruleType(entity.getRuleType())
                .ruleTypeDisplayName(entity.getRuleType() != null ? entity.getRuleType().getDisplayName() : null)
                .severity(entity.getSeverity())
                .severityDisplayName(entity.getSeverity() != null ? entity.getSeverity().getDisplayName() : null)
                .active(entity.getActive())
                .description(entity.getDescription())
                .thresholdAmount(entity.getThresholdAmount())
                .thresholdCurrency(entity.getThresholdCurrency())
                .maxTransactions(entity.getMaxTransactions())
                .timeWindowMinutes(entity.getTimeWindowMinutes())
                .dailyLimitAmount(entity.getDailyLimitAmount())
                .lookbackDays(entity.getLookbackDays())
                .alertCount(entity.getAlerts() != null ? entity.getAlerts().size() : 0)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Update existing MonitoringRule entity from MonitoringRuleRequestDTO.
     */
    public void updateEntityFromDTO(MonitoringRuleRequestDTO dto, MonitoringRule entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setRuleName(dto.getRuleName());
        entity.setRuleType(dto.getRuleType());
        entity.setSeverity(dto.getSeverity());
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
        entity.setDescription(dto.getDescription());
        entity.setThresholdAmount(dto.getThresholdAmount());
        entity.setThresholdCurrency(dto.getThresholdCurrency());
        entity.setMaxTransactions(dto.getMaxTransactions());
        entity.setTimeWindowMinutes(dto.getTimeWindowMinutes());
        entity.setDailyLimitAmount(dto.getDailyLimitAmount());
        entity.setLookbackDays(dto.getLookbackDays());
    }
}
