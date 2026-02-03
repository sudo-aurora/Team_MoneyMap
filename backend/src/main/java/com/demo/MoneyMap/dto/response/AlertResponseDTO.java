package com.demo.MoneyMap.dto.response;

import com.demo.MoneyMap.entity.enums.AlertSeverity;
import com.demo.MoneyMap.entity.enums.AlertStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for alert response data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload containing alert information")
public class AlertResponseDTO {

    @Schema(description = "Unique identifier of the alert", example = "1")
    private Long id;

    @Schema(description = "Alert reference number", example = "ALERT-12345678")
    private String alertReference;

    @Schema(description = "Rule ID that triggered the alert", example = "1")
    private Long ruleId;

    @Schema(description = "Rule name that triggered the alert", example = "High Value Transaction")
    private String ruleName;

    @Schema(description = "Alert severity", example = "HIGH")
    private AlertSeverity severity;

    @Schema(description = "Severity display name", example = "High")
    private String severityDisplayName;

    @Schema(description = "Alert status", example = "OPEN")
    private AlertStatus status;

    @Schema(description = "Status display name", example = "Open")
    private String statusDisplayName;

    @Schema(description = "Alert message", example = "Transaction amount exceeds threshold of $10,000")
    private String message;

    @Schema(description = "Account ID related to the alert", example = "ACC-001-123456")
    private String accountId;

    @Schema(description = "IDs of payments that triggered the alert")
    private List<Long> triggeringPaymentIds;

    @Schema(description = "Payments that triggered the alert")
    private List<PaymentResponseDTO> triggeringPayments;

    @Schema(description = "Timestamp when the alert was acknowledged")
    private LocalDateTime acknowledgedAt;

    @Schema(description = "Operator who acknowledged the alert")
    private String acknowledgedBy;

    @Schema(description = "Timestamp when the alert was closed")
    private LocalDateTime closedAt;

    @Schema(description = "Operator who closed the alert")
    private String closedBy;

    @Schema(description = "Resolution notes")
    private String resolutionNotes;

    @Schema(description = "Timestamp when the alert was created")
    private LocalDateTime createdAt;
}
