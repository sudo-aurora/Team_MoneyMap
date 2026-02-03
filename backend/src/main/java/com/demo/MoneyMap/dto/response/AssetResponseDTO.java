package com.demo.MoneyMap.dto.response;

import com.demo.MoneyMap.entity.enums.AssetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for asset response.
 * Includes type-specific fields based on asset type.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Asset response data")
public class AssetResponseDTO {

    @Schema(description = "Asset ID", example = "1")
    private Long id;

    @Schema(description = "Asset name", example = "Apple Inc.")
    private String name;

    @Schema(description = "Trading symbol", example = "AAPL")
    private String symbol;

    @Schema(description = "Asset type", example = "STOCK")
    private AssetType assetType;

    @Schema(description = "Quantity owned", example = "100")
    private BigDecimal quantity;

    @Schema(description = "Purchase price", example = "150.00")
    private BigDecimal purchasePrice;

    @Schema(description = "Current market price", example = "175.50")
    private BigDecimal currentPrice;

    @Schema(description = "Current total value", example = "17550.00")
    private BigDecimal currentValue;

    @Schema(description = "Purchase date", example = "2024-01-15")
    private LocalDate purchaseDate;

    @Schema(description = "Portfolio ID", example = "1")
    private Long portfolioId;

    @Schema(description = "Notes", example = "Long-term hold")
    private String notes;

    @Schema(description = "When the asset was added")
    private LocalDateTime createdAt;

    @Schema(description = "Last updated")
    private LocalDateTime updatedAt;

    @Schema(description = "Profit/Loss amount")
    private BigDecimal profitLoss;

    @Schema(description = "Profit/Loss percentage")
    private BigDecimal profitLossPercentage;

    // Stock-specific fields
    @Schema(description = "Stock exchange (STOCK only)", example = "NASDAQ")
    private String exchange;

    @Schema(description = "Industry sector (STOCK only)", example = "Technology")
    private String sector;

    @Schema(description = "Dividend yield (STOCK only)", example = "0.55")
    private BigDecimal dividendYield;

    @Schema(description = "Fractional shares allowed (STOCK only)", example = "true")
    private Boolean fractionalAllowed;

    // Crypto-specific fields
    @Schema(description = "Blockchain network (CRYPTO only)", example = "Ethereum")
    private String blockchain;

    @Schema(description = "Wallet address (CRYPTO only)", example = "0x...")
    private String walletAddress;

    @Schema(description = "Staking enabled (CRYPTO only)", example = "true")
    private Boolean stakingEnabled;

    @Schema(description = "Staking APY (CRYPTO only)", example = "4.5")
    private BigDecimal stakingAPY;

    // Gold-specific fields
    @Schema(description = "Gold purity (GOLD only)", example = "24K")
    private String purity;

    @Schema(description = "Weight in grams (GOLD only)", example = "31.1")
    private BigDecimal weightInGrams;

    @Schema(description = "Storage location (GOLD only)", example = "Bank Locker #123")
    private String storageLocation;

    @Schema(description = "Certificate number (GOLD only)", example = "CERT-123456")
    private String certificateNumber;

    @Schema(description = "Physical gold (GOLD only)", example = "true")
    private Boolean isPhysical;

    // Mutual Fund-specific fields
    @Schema(description = "Fund manager (MUTUAL_FUND only)", example = "Vanguard")
    private String fundManager;

    @Schema(description = "Expense ratio (MUTUAL_FUND only)", example = "0.04")
    private BigDecimal expenseRatio;

    @Schema(description = "NAV price (MUTUAL_FUND only)", example = "425.00")
    private BigDecimal navPrice;

    @Schema(description = "Minimum investment (MUTUAL_FUND only)", example = "3000.00")
    private BigDecimal minimumInvestment;

    @Schema(description = "Risk level (MUTUAL_FUND only)", example = "Moderate")
    private String riskLevel;
}
