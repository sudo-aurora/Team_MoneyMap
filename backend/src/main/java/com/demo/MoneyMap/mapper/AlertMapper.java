package com.demo.MoneyMap.mapper;

import com.demo.MoneyMap.dto.response.AlertResponseDTO;
import com.demo.MoneyMap.dto.response.PaymentResponseDTO;
import com.demo.MoneyMap.entity.Alert;
import com.demo.MoneyMap.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for Alert entity and DTOs.
 */
@Component
@RequiredArgsConstructor
public class AlertMapper {

    private final PaymentMapper paymentMapper;

    /**
     * Convert Alert entity to AlertResponseDTO (without payment details).
     */
    public AlertResponseDTO toResponseDTO(Alert entity) {
        if (entity == null) {
            return null;
        }

        List<Long> triggeringPaymentIds = entity.getTriggeringPayments() != null
                ? entity.getTriggeringPayments().stream()
                        .map(Payment::getId)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return AlertResponseDTO.builder()
                .id(entity.getId())
                .alertReference(entity.getAlertReference())
                .ruleId(entity.getRule() != null ? entity.getRule().getId() : null)
                .ruleName(entity.getRule() != null ? entity.getRule().getRuleName() : null)
                .severity(entity.getSeverity())
                .severityDisplayName(entity.getSeverity() != null ? entity.getSeverity().getDisplayName() : null)
                .status(entity.getStatus())
                .statusDisplayName(entity.getStatus() != null ? entity.getStatus().getDisplayName() : null)
                .message(entity.getMessage())
                .accountId(entity.getAccountId())
                .triggeringPaymentIds(triggeringPaymentIds)
                .triggeringPayments(Collections.emptyList())
                .acknowledgedAt(entity.getAcknowledgedAt())
                .acknowledgedBy(entity.getAcknowledgedBy())
                .closedAt(entity.getClosedAt())
                .closedBy(entity.getClosedBy())
                .resolutionNotes(entity.getResolutionNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Convert Alert entity to AlertResponseDTO (with full payment details).
     */
    public AlertResponseDTO toResponseDTOWithDetails(Alert entity) {
        if (entity == null) {
            return null;
        }

        List<Long> triggeringPaymentIds = entity.getTriggeringPayments() != null
                ? entity.getTriggeringPayments().stream()
                        .map(Payment::getId)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        List<PaymentResponseDTO> triggeringPayments = entity.getTriggeringPayments() != null
                ? entity.getTriggeringPayments().stream()
                        .map(paymentMapper::toResponseDTO)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return AlertResponseDTO.builder()
                .id(entity.getId())
                .alertReference(entity.getAlertReference())
                .ruleId(entity.getRule() != null ? entity.getRule().getId() : null)
                .ruleName(entity.getRule() != null ? entity.getRule().getRuleName() : null)
                .severity(entity.getSeverity())
                .severityDisplayName(entity.getSeverity() != null ? entity.getSeverity().getDisplayName() : null)
                .status(entity.getStatus())
                .statusDisplayName(entity.getStatus() != null ? entity.getStatus().getDisplayName() : null)
                .message(entity.getMessage())
                .accountId(entity.getAccountId())
                .triggeringPaymentIds(triggeringPaymentIds)
                .triggeringPayments(triggeringPayments)
                .acknowledgedAt(entity.getAcknowledgedAt())
                .acknowledgedBy(entity.getAcknowledgedBy())
                .closedAt(entity.getClosedAt())
                .closedBy(entity.getClosedBy())
                .resolutionNotes(entity.getResolutionNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
