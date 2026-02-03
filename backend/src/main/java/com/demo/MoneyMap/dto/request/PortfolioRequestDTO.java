package com.demo.MoneyMap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for creating or updating a portfolio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a portfolio")
public class PortfolioRequestDTO {

    @NotBlank(message = "Portfolio name is required")
    @Size(max = 100, message = "Portfolio name cannot exceed 100 characters")
    @Schema(description = "Name of the portfolio", example = "Growth Portfolio", required = true)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Description of the portfolio", example = "Focused on high-growth tech stocks and crypto")
    private String description;

    @NotNull(message = "Client ID is required")
    @Schema(description = "ID of the client who owns this portfolio", example = "1", required = true)
    private Long clientId;
}
