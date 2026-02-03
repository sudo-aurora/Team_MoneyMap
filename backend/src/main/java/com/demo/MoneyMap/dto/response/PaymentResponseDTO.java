package com.demo.MoneyMap.dto.response;

import com.demo.MoneyMap.entity.enums.PaymentErrorCode;
import com.demo.MoneyMap.entity.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for payment response data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload containing payment information")
public class PaymentResponseDTO {

    @Schema(description = "Unique identifier of the payment", example = "1")
    private Long id;

    @Schema(description = "Payment reference number", example = "PAY-12345678")
    private String paymentReference;

    @Schema(description = "Idempotency key", example = "idem-key-12345")
    private String idempotencyKey;

    @Schema(description = "Source account identifier", example = "ACC-001-123456")
    private String sourceAccount;

    @Schema(description = "Destination account identifier", example = "ACC-002-789012")
    private String destinationAccount;

    @Schema(description = "Payment amount", example = "1500.00")
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Schema(description = "Current payment status", example = "VALIDATED")
    private PaymentStatus status;

    @Schema(description = "Status display name", example = "Validated")
    private String statusDisplayName;

    @Schema(description = "Error code if payment failed")
    private PaymentErrorCode errorCode;

    @Schema(description = "Error message if payment failed")
    private String errorMessage;

    @Schema(description = "Payment reference", example = "Invoice-2026-001")
    private String reference;

    @Schema(description = "Payment description", example = "Payment for consulting services")
    private String description;

    @Schema(description = "Payment status history")
    private List<PaymentStatusHistoryDTO> statusHistory;

    @Schema(description = "Timestamp when the payment was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the payment was last updated")
    private LocalDateTime updatedAt;
}
