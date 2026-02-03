package com.demo.MoneyMap.mapper;

import com.demo.MoneyMap.dto.request.PaymentRequestDTO;
import com.demo.MoneyMap.dto.response.PaymentResponseDTO;
import com.demo.MoneyMap.dto.response.PaymentStatusHistoryDTO;
import com.demo.MoneyMap.entity.Payment;
import com.demo.MoneyMap.entity.PaymentStatusHistory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for Payment entity and DTOs.
 */
@Component
public class PaymentMapper {

    /**
     * Convert PaymentRequestDTO to Payment entity.
     */
    public Payment toEntity(PaymentRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Payment.builder()
                .idempotencyKey(dto.getIdempotencyKey())
                .sourceAccount(dto.getSourceAccount())
                .destinationAccount(dto.getDestinationAccount())
                .amount(dto.getAmount())
                .currency(dto.getCurrency().toUpperCase())
                .reference(dto.getReference())
                .description(dto.getDescription())
                .build();
    }

    /**
     * Convert Payment entity to PaymentResponseDTO (without history).
     */
    public PaymentResponseDTO toResponseDTO(Payment entity) {
        if (entity == null) {
            return null;
        }

        return PaymentResponseDTO.builder()
                .id(entity.getId())
                .paymentReference(entity.getPaymentReference())
                .idempotencyKey(entity.getIdempotencyKey())
                .sourceAccount(entity.getSourceAccount())
                .destinationAccount(entity.getDestinationAccount())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .statusDisplayName(entity.getStatus() != null ? entity.getStatus().getDisplayName() : null)
                .errorCode(entity.getErrorCode())
                .errorMessage(entity.getErrorMessage())
                .reference(entity.getReference())
                .description(entity.getDescription())
                .statusHistory(Collections.emptyList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert Payment entity to PaymentResponseDTO (with history).
     */
    public PaymentResponseDTO toResponseDTOWithHistory(Payment entity) {
        if (entity == null) {
            return null;
        }

        List<PaymentStatusHistoryDTO> historyDTOs = entity.getStatusHistory() != null
                ? entity.getStatusHistory().stream()
                        .map(this::toHistoryDTO)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return PaymentResponseDTO.builder()
                .id(entity.getId())
                .paymentReference(entity.getPaymentReference())
                .idempotencyKey(entity.getIdempotencyKey())
                .sourceAccount(entity.getSourceAccount())
                .destinationAccount(entity.getDestinationAccount())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .statusDisplayName(entity.getStatus() != null ? entity.getStatus().getDisplayName() : null)
                .errorCode(entity.getErrorCode())
                .errorMessage(entity.getErrorMessage())
                .reference(entity.getReference())
                .description(entity.getDescription())
                .statusHistory(historyDTOs)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert PaymentStatusHistory to DTO.
     */
    public PaymentStatusHistoryDTO toHistoryDTO(PaymentStatusHistory entity) {
        if (entity == null) {
            return null;
        }

        return PaymentStatusHistoryDTO.builder()
                .id(entity.getId())
                .previousStatus(entity.getPreviousStatus())
                .status(entity.getStatus())
                .statusDisplayName(entity.getStatus() != null ? entity.getStatus().getDisplayName() : null)
                .notes(entity.getNotes())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
