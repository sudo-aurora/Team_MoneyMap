package com.demo.MoneyMap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

/**
 * Lightweight DTO for portfolio summary information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Summary information of a portfolio")
public class PortfolioSummaryDTO {

    @Schema(description = "Unique identifier of the portfolio", example = "1")
    private Long id;

    @Schema(description = "Name of the portfolio", example = "Growth Portfolio")
    private String name;

    @Schema(description = "Description of the portfolio", example = "High-growth tech stocks")
    private String description;

    @Schema(description = "Total value of the portfolio", example = "150000.00")
    private BigDecimal totalValue;

    @Schema(description = "Number of assets in the portfolio", example = "10")
    private Integer assetCount;

    @Schema(description = "Whether the portfolio is active", example = "true")
    private Boolean active;
}
