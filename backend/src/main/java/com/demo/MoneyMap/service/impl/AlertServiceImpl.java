package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.dto.request.AlertStatusUpdateDTO;
import com.demo.MoneyMap.dto.response.AlertResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.Alert;
import com.demo.MoneyMap.entity.enums.AlertSeverity;
import com.demo.MoneyMap.entity.enums.AlertStatus;
import com.demo.MoneyMap.exception.BadRequestException;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.mapper.AlertMapper;
import com.demo.MoneyMap.repository.AlertRepository;
import com.demo.MoneyMap.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of AlertService.
 * Manages alert lifecycle and queries.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;

    @Override
    @Transactional(readOnly = true)
    public AlertResponseDTO getAlertById(Long id) {
        Alert alert = findAlertById(id);
        return alertMapper.toResponseDTO(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public AlertResponseDTO getAlertWithDetails(Long id) {
        Alert alert = alertRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with ID: " + id));
        return alertMapper.toResponseDTOWithDetails(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AlertResponseDTO> getAllAlerts(Pageable pageable) {
        Page<Alert> alertPage = alertRepository.findAll(pageable);
        return mapToPagedResponse(alertPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AlertResponseDTO> getAlertsByStatus(AlertStatus status, Pageable pageable) {
        Page<Alert> alertPage = alertRepository.findByStatus(status, pageable);
        return mapToPagedResponse(alertPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AlertResponseDTO> getAlertsBySeverity(AlertSeverity severity, Pageable pageable) {
        Page<Alert> alertPage = alertRepository.findBySeverity(severity, pageable);
        return mapToPagedResponse(alertPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AlertResponseDTO> getAlertsByStatusAndSeverity(AlertStatus status, AlertSeverity severity, Pageable pageable) {
        Page<Alert> alertPage = alertRepository.findByStatusAndSeverity(status, severity, pageable);
        return mapToPagedResponse(alertPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AlertResponseDTO> getAlertsByRuleId(Long ruleId, Pageable pageable) {
        Page<Alert> alertPage = alertRepository.findByRuleId(ruleId, pageable);
        return mapToPagedResponse(alertPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AlertResponseDTO> getAlertsByAccountId(String accountId, Pageable pageable) {
        Page<Alert> alertPage = alertRepository.findByAccountId(accountId, pageable);
        return mapToPagedResponse(alertPage);
    }

    @Override
    public AlertResponseDTO acknowledgeAlert(Long id, String operatorName) {
        log.info("Acknowledging alert {} by {}", id, operatorName);
        
        Alert alert = findAlertById(id);
        
        if (alert.getStatus() != AlertStatus.OPEN) {
            throw new BadRequestException("Only OPEN alerts can be acknowledged");
        }

        alert.acknowledge(operatorName);
        Alert updatedAlert = alertRepository.save(alert);

        log.info("Successfully acknowledged alert {}", id);
        return alertMapper.toResponseDTO(updatedAlert);
    }

    @Override
    public AlertResponseDTO updateAlertStatus(Long id, AlertStatusUpdateDTO updateDTO) {
        log.info("Updating alert {} status to {}", id, updateDTO.getStatus());

        Alert alert = findAlertById(id);
        AlertStatus currentStatus = alert.getStatus();
        AlertStatus newStatus = updateDTO.getStatus();

        // Validate status transition
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new BadRequestException(String.format(
                    "Invalid status transition from %s to %s", currentStatus, newStatus));
        }

        switch (newStatus) {
            case ACKNOWLEDGED:
                alert.acknowledge(updateDTO.getOperatorName());
                break;
            case INVESTIGATING:
                alert.setStatus(AlertStatus.INVESTIGATING);
                break;
            case CLOSED:
                alert.close(updateDTO.getOperatorName(), updateDTO.getResolutionNotes());
                break;
            case DISMISSED:
                alert.dismiss(updateDTO.getOperatorName(), updateDTO.getResolutionNotes());
                break;
            default:
                throw new BadRequestException("Invalid status: " + newStatus);
        }

        Alert updatedAlert = alertRepository.save(alert);
        log.info("Successfully updated alert {} status to {}", id, newStatus);
        return alertMapper.toResponseDTO(updatedAlert);
    }

    @Override
    public AlertResponseDTO addResolutionNotes(Long id, String notes) {
        log.info("Adding resolution notes to alert {}", id);
        
        Alert alert = findAlertById(id);
        alert.setResolutionNotes(notes);
        Alert updatedAlert = alertRepository.save(alert);

        return alertMapper.toResponseDTO(updatedAlert);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponseDTO> getOpenAlertsPrioritized(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return alertRepository.findOpenAlertsPrioritized(pageable).stream()
                .map(alertMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Counts by status
        Map<String, Long> statusCounts = new HashMap<>();
        for (AlertStatus status : AlertStatus.values()) {
            statusCounts.put(status.name(), alertRepository.countByStatus(status));
        }
        stats.put("byStatus", statusCounts);

        // Counts by severity
        Map<String, Long> severityCounts = new HashMap<>();
        for (AlertSeverity severity : AlertSeverity.values()) {
            severityCounts.put(severity.name(), alertRepository.countBySeverity(severity));
        }
        stats.put("bySeverity", severityCounts);

        // Open alerts by severity
        Map<String, Long> openBySeverity = new HashMap<>();
        for (AlertSeverity severity : AlertSeverity.values()) {
            openBySeverity.put(severity.name(), alertRepository.countByStatusAndSeverity(AlertStatus.OPEN, severity));
        }
        stats.put("openBySeverity", openBySeverity);

        // Average acknowledge time
        Double avgAckTime = alertRepository.getAverageAcknowledgeTime();
        stats.put("averageAcknowledgeTimeSeconds", avgAckTime != null ? avgAckTime : 0);

        // Alerts created today
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        stats.put("alertsToday", alertRepository.countAlertsCreatedSince(startOfDay));

        // Total alerts
        stats.put("totalAlerts", alertRepository.count());

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public long countAlertsByStatus(AlertStatus status) {
        return alertRepository.countByStatus(status);
    }

    /**
     * Find alert by ID or throw exception.
     */
    private Alert findAlertById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with ID: " + id));
    }

    /**
     * Map Page to PagedResponseDTO.
     */
    private PagedResponseDTO<AlertResponseDTO> mapToPagedResponse(Page<Alert> page) {
        List<AlertResponseDTO> content = page.getContent().stream()
                .map(alertMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PagedResponseDTO.<AlertResponseDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
