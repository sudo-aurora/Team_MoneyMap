package com.demo.MoneyMap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for portfolio response data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload containing portfolio information")
public class PortfolioResponseDTO {

    @Schema(description = "Unique identifier of the portfolio", example = "1")
    private Long id;

    @Schema(description = "Name of the portfolio", example = "Growth Portfolio")
    private String name;

    @Schema(description = "Description of the portfolio", example = "Focused on high-growth tech stocks and crypto")
    private String description;

    @Schema(description = "ID of the client who owns this portfolio", example = "1")
    private Long clientId;

    @Schema(description = "Name of the client who owns this portfolio", example = "John Doe")
    private String clientName;

    @Schema(description = "Total value of the portfolio", example = "150000.00")
    private BigDecimal totalValue;

    @Schema(description = "Whether the portfolio is active", example = "true")
    private Boolean active;

    @Schema(description = "Number of assets in the portfolio", example = "10")
    private Integer assetCount;

    @Schema(description = "List of assets in this portfolio")
    private List<AssetSummaryDTO> assets;

    @Schema(description = "Timestamp when the portfolio was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the portfolio was last updated")
    private LocalDateTime updatedAt;
}
