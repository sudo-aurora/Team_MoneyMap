package com.demo.MoneyMap.dto.response;

import com.demo.MoneyMap.entity.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for transaction response data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload containing transaction information")
public class TransactionResponseDTO {

    @Schema(description = "Unique identifier of the transaction", example = "1")
    private Long id;

    @Schema(description = "Type of the transaction", example = "BUY")
    private TransactionType transactionType;

    @Schema(description = "Display name of the transaction type", example = "Buy")
    private String transactionTypeDisplayName;

    @Schema(description = "Quantity of assets in the transaction", example = "50.0")
    private BigDecimal quantity;

    @Schema(description = "Price per unit at the time of transaction", example = "175.50")
    private BigDecimal pricePerUnit;

    @Schema(description = "Total amount of the transaction", example = "8784.99")
    private BigDecimal totalAmount;

    @Schema(description = "Transaction fees", example = "9.99")
    private BigDecimal fees;

    @Schema(description = "ID of the asset this transaction is for", example = "1")
    private Long assetId;

    @Schema(description = "Name of the asset", example = "Apple Inc.")
    private String assetName;

    @Schema(description = "Symbol of the asset", example = "AAPL")
    private String assetSymbol;

    @Schema(description = "Date and time of the transaction")
    private LocalDateTime transactionDate;

    @Schema(description = "Additional notes about the transaction", example = "Quarterly investment")
    private String notes;

    @Schema(description = "Timestamp when the transaction was recorded")
    private LocalDateTime createdAt;
}
