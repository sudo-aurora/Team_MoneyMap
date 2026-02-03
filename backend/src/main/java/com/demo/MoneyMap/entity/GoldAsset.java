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
 * Gold asset subclass with gold-specific fields.
 * Demonstrates Inheritance OOP concept.
 */
@Entity
@DiscriminatorValue("GOLD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GoldAsset extends Asset {

    @Column(length = 10)
    private String purity; // e.g., 24K, 22K, 18K

    @Column(precision = 10, scale = 2)
    private BigDecimal weightInGrams; // Weight in grams

    @Column(length = 200)
    private String storageLocation; // e.g., "Bank Locker #123", "Home Safe"

    @Column(length = 100)
    private String certificateNumber; // Certificate number for physical gold

    @Column
    private Boolean isPhysical; // Physical gold vs. gold ETF

    @Override
    public AssetType getType() {
        return AssetType.GOLD;
    }

    @Override
    public Set<TransactionType> getAllowedTransactionTypes() {
        return Set.of(
                TransactionType.BUY,
                TransactionType.SELL,
                TransactionType.TRANSFER_IN,
                TransactionType.TRANSFER_OUT
        );
    }

    @Override
    public String getTypeDescription() {
        return "Gold " + (purity != null ? purity + " purity" : "") +
                (Boolean.TRUE.equals(isPhysical) ? " (Physical)" : " (ETF/Paper)");
    }

    @Override
    public boolean isQuantityValid(BigDecimal quantity) {
        // Gold can be in fractions (grams, ounces)
        return quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public BigDecimal getMinimumQuantityIncrement() {
        return new BigDecimal("0.01"); // Can buy as little as 0.01 grams
    }
}
