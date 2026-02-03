package com.demo.MoneyMap.dto.request;

import com.demo.MoneyMap.entity.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for creating or updating a transaction.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a transaction")
public class TransactionRequestDTO {

    @NotNull(message = "Transaction type is required")
    @Schema(description = "Type of the transaction", example = "BUY", required = true)
    private TransactionType transactionType;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00000001", message = "Quantity must be greater than 0")
    @Schema(description = "Quantity of assets in the transaction", example = "50.0", required = true)
    private BigDecimal quantity;

    @NotNull(message = "Price per unit is required")
    @DecimalMin(value = "0.0001", message = "Price per unit must be greater than 0")
    @Schema(description = "Price per unit at the time of transaction", example = "175.50", required = true)
    private BigDecimal pricePerUnit;

    @DecimalMin(value = "0", message = "Fees cannot be negative")
    @Schema(description = "Transaction fees", example = "9.99")
    private BigDecimal fees;

    @NotNull(message = "Asset ID is required")
    @Schema(description = "ID of the asset this transaction is for", example = "1", required = true)
    private Long assetId;

    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date cannot be in the future")
    @Schema(description = "Date and time of the transaction", example = "2024-06-15T10:30:00", required = true)
    private LocalDateTime transactionDate;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Schema(description = "Additional notes about the transaction", example = "Quarterly investment")
    private String notes;
}
