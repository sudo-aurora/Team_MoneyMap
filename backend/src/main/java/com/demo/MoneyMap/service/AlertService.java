package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.AlertStatusUpdateDTO;
import com.demo.MoneyMap.dto.response.AlertResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.enums.AlertSeverity;
import com.demo.MoneyMap.entity.enums.AlertStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for alert management operations.
 */
public interface AlertService {

    /**
     * Get alert by ID.
     */
    AlertResponseDTO getAlertById(Long id);

    /**
     * Get alert by ID with full details.
     */
    AlertResponseDTO getAlertWithDetails(Long id);

    /**
     * Get all alerts with pagination.
     */
    PagedResponseDTO<AlertResponseDTO> getAllAlerts(Pageable pageable);

    /**
     * Get alerts by status.
     */
    PagedResponseDTO<AlertResponseDTO> getAlertsByStatus(AlertStatus status, Pageable pageable);

    /**
     * Get alerts by severity.
     */
    PagedResponseDTO<AlertResponseDTO> getAlertsBySeverity(AlertSeverity severity, Pageable pageable);

    /**
     * Get alerts by status and severity.
     */
    PagedResponseDTO<AlertResponseDTO> getAlertsByStatusAndSeverity(AlertStatus status, AlertSeverity severity, Pageable pageable);

    /**
     * Get alerts by rule ID.
     */
    PagedResponseDTO<AlertResponseDTO> getAlertsByRuleId(Long ruleId, Pageable pageable);

    /**
     * Get alerts by account ID.
     */
    PagedResponseDTO<AlertResponseDTO> getAlertsByAccountId(String accountId, Pageable pageable);

    /**
     * Acknowledge an alert.
     */
    AlertResponseDTO acknowledgeAlert(Long id, String operatorName);

    /**
     * Update alert status.
     */
    AlertResponseDTO updateAlertStatus(Long id, AlertStatusUpdateDTO updateDTO);

    /**
     * Add resolution notes to an alert.
     */
    AlertResponseDTO addResolutionNotes(Long id, String notes);

    /**
     * Get open alerts prioritized by severity.
     */
    List<AlertResponseDTO> getOpenAlertsPrioritized(int limit);

    /**
     * Get alert statistics.
     */
    Map<String, Object> getAlertStatistics();

    /**
     * Count alerts by status.
     */
    long countAlertsByStatus(AlertStatus status);
}
