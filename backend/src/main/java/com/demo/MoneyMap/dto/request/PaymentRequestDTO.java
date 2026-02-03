package com.demo.MoneyMap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for creating a new payment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating a new payment")
public class PaymentRequestDTO {

    @Size(max = 100, message = "Idempotency key cannot exceed 100 characters")
    @Schema(description = "Unique key to prevent duplicate payments", example = "idem-key-12345")
    private String idempotencyKey;

    @NotBlank(message = "Source account is required")
    @Size(max = 50, message = "Source account cannot exceed 50 characters")
    @Schema(description = "Source account identifier", example = "ACC-001-123456", required = true)
    private String sourceAccount;

    @NotBlank(message = "Destination account is required")
    @Size(max = 50, message = "Destination account cannot exceed 50 characters")
    @Schema(description = "Destination account identifier", example = "ACC-002-789012", required = true)
    private String destinationAccount;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Amount cannot exceed 1,000,000")
    @Digits(integer = 10, fraction = 2, message = "Amount can have maximum 2 decimal places")
    @Schema(description = "Payment amount", example = "1500.00", required = true)
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    @Schema(description = "Currency code (ISO 4217)", example = "USD", required = true)
    private String currency;

    @Size(max = 200, message = "Reference cannot exceed 200 characters")
    @Schema(description = "Payment reference", example = "Invoice-2026-001")
    private String reference;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Payment description", example = "Payment for consulting services")
    private String description;
}
