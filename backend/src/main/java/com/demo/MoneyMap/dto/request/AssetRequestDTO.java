package com.demo.MoneyMap.dto.request;

import com.demo.MoneyMap.entity.enums.AssetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating or updating an asset.
 * Includes type-specific optional fields for Stock, Crypto, Gold, and Mutual Fund assets.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating an asset")
public class AssetRequestDTO {

    // Common fields for all assets
    @NotBlank(message = "Asset name is required")
    @Size(max = 150, message = "Asset name must not exceed 150 characters")
    @Schema(description = "Name of the asset", example = "Apple Inc.", required = true)
    private String name;

    @NotBlank(message = "Symbol is required")
    @Size(max = 20, message = "Symbol must not exceed 20 characters")
    @Schema(description = "Trading symbol/ticker", example = "AAPL", required = true)
    private String symbol;

    @NotNull(message = "Asset type is required")
    @Schema(description = "Type of asset", example = "STOCK", required = true)
    private AssetType assetType;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
    @Schema(description = "Quantity/amount of the asset", example = "100", required = true)
    private BigDecimal quantity;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be greater than 0")
    @Schema(description = "Price at which the asset was purchased", example = "150.00", required = true)
    private BigDecimal purchasePrice;

    @NotNull(message = "Current price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Current price must be greater than 0")
    @Schema(description = "Current market price of the asset", example = "175.50", required = true)
    private BigDecimal currentPrice;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    @Schema(description = "Date when the asset was purchased", example = "2024-01-15", required = true)
    private LocalDate purchaseDate;

    @NotNull(message = "Portfolio ID is required")
    @Schema(description = "ID of the portfolio this asset belongs to", example = "1", required = true)
    private Long portfolioId;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Schema(description = "Additional notes about the asset", example = "Long-term investment")
    private String notes;

    // Stock-specific fields
    @Size(max = 50, message = "Exchange must not exceed 50 characters")
    @Schema(description = "Stock exchange (for STOCK type)", example = "NASDAQ")
    private String exchange;

    @Size(max = 100, message = "Sector must not exceed 100 characters")
    @Schema(description = "Industry sector (for STOCK type)", example = "Technology")
    private String sector;

    @DecimalMin(value = "0.0", message = "Dividend yield must be non-negative")
    @Schema(description = "Dividend yield percentage (for STOCK type)", example = "0.55")
    private BigDecimal dividendYield;

    @Schema(description = "Whether fractional shares are allowed (for STOCK type)", example = "true")
    private Boolean fractionalAllowed;

    // Crypto-specific fields
    @Size(max = 50, message = "Blockchain must not exceed 50 characters")
    @Schema(description = "Blockchain network (for CRYPTO type)", example = "Ethereum")
    private String blockchain;

    @Size(max = 200, message = "Wallet address must not exceed 200 characters")
    @Schema(description = "Wallet address (for CRYPTO type)", example = "0x...")
    private String walletAddress;

    @Schema(description = "Whether staking is enabled (for CRYPTO type)", example = "true")
    private Boolean stakingEnabled;

    @DecimalMin(value = "0.0", message = "Staking APY must be non-negative")
    @Schema(description = "Staking annual percentage yield (for CRYPTO type)", example = "4.5")
    private BigDecimal stakingAPY;

    // Gold-specific fields
    @Size(max = 10, message = "Purity must not exceed 10 characters")
    @Schema(description = "Gold purity (for GOLD type)", example = "24K")
    private String purity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than 0")
    @Schema(description = "Weight in grams (for GOLD type)", example = "31.1")
    private BigDecimal weightInGrams;

    @Size(max = 200, message = "Storage location must not exceed 200 characters")
    @Schema(description = "Where the gold is stored (for GOLD type)", example = "Bank Locker #123")
    private String storageLocation;

    @Size(max = 100, message = "Certificate number must not exceed 100 characters")
    @Schema(description = "Certificate number (for GOLD type)", example = "CERT-123456")
    private String certificateNumber;

    @Schema(description = "Whether it's physical gold (for GOLD type)", example = "true")
    private Boolean isPhysical;

    // Mutual Fund-specific fields
    @Size(max = 200, message = "Fund manager must not exceed 200 characters")
    @Schema(description = "Fund manager/company (for MUTUAL_FUND type)", example = "Vanguard")
    private String fundManager;

    @DecimalMin(value = "0.0", message = "Expense ratio must be non-negative")
    @Schema(description = "Fund expense ratio (for MUTUAL_FUND type)", example = "0.04")
    private BigDecimal expenseRatio;

    @DecimalMin(value = "0.0", inclusive = false, message = "NAV price must be greater than 0")
    @Schema(description = "Net Asset Value per unit (for MUTUAL_FUND type)", example = "425.00")
    private BigDecimal navPrice;

    @DecimalMin(value = "0.0", message = "Minimum investment must be non-negative")
    @Schema(description = "Minimum investment amount (for MUTUAL_FUND type)", example = "3000.00")
    private BigDecimal minimumInvestment;

    @Size(max = 50, message = "Risk level must not exceed 50 characters")
    @Schema(description = "Risk level (for MUTUAL_FUND type)", example = "Moderate")
    private String riskLevel;
}
