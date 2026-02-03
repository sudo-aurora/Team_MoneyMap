package com.demo.MoneyMap.entity;

import com.demo.MoneyMap.entity.enums.AssetType;
import com.demo.MoneyMap.entity.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Mutual Fund asset subclass with fund-specific fields.
 * Demonstrates Inheritance OOP concept.
 */
@Entity
@DiscriminatorValue("MUTUAL_FUND")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MutualFundAsset extends Asset {

    @Column(length = 200)
    private String fundManager; // Name of fund manager/company

    @Column(precision = 5, scale = 2)
    private BigDecimal expenseRatio; // Fund expense ratio (e.g., 0.50 for 0.5%)

    @Column(precision = 15, scale = 2)
    private BigDecimal navPrice; // Net Asset Value per unit

    @Column(precision = 15, scale = 2)
    private BigDecimal minimumInvestment; // Minimum amount to invest

    @Column(length = 50)
    private String riskLevel; // e.g., Low, Moderate, High

    @Override
    public AssetType getType() {
        return AssetType.MUTUAL_FUND;
    }

    @Override
    public Set<TransactionType> getAllowedTransactionTypes() {
        return Set.of(
                TransactionType.BUY,
                TransactionType.SELL,
                TransactionType.DIVIDEND,
                TransactionType.TRANSFER_IN,
                TransactionType.TRANSFER_OUT
        );
    }

    @Override
    public String getTypeDescription() {
        return "Mutual Fund managed by " + (fundManager != null ? fundManager : "fund house") +
                (riskLevel != null ? " (" + riskLevel + " risk)" : "");
    }

    @Override
    public boolean isQuantityValid(BigDecimal quantity) {
        // Mutual funds typically allow fractional units
        return quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public BigDecimal getMinimumQuantityIncrement() {
        return new BigDecimal("0.001"); // Can buy fractional units
    }
}
