package com.demo.MoneyMap.dto.response;

import com.demo.MoneyMap.entity.enums.AssetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

/**
 * Lightweight DTO for asset summary information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Summary information of an asset")
public class AssetSummaryDTO {

    @Schema(description = "Unique identifier of the asset", example = "1")
    private Long id;

    @Schema(description = "Name of the asset", example = "Apple Inc.")
    private String name;

    @Schema(description = "Symbol/ticker of the asset", example = "AAPL")
    private String symbol;

    @Schema(description = "Type of the asset", example = "STOCK")
    private AssetType assetType;

    @Schema(description = "Quantity of the asset held", example = "100")
    private BigDecimal quantity;

    @Schema(description = "Current market price", example = "175.50")
    private BigDecimal currentPrice;

    @Schema(description = "Current value of the asset", example = "15000.00")
    private BigDecimal currentValue;

    @Schema(description = "Profit/Loss amount")
    private BigDecimal profitLoss;

    @Schema(description = "Profit/Loss percentage")
    private BigDecimal profitLossPercentage;
}
