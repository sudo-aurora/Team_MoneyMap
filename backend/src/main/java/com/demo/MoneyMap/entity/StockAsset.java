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
 * Stock asset subclass with stock-specific fields.
 * Demonstrates Inheritance OOP concept.
 */
@Entity
@DiscriminatorValue("STOCK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StockAsset extends Asset {

    @Column(length = 50)
    private String exchange; // e.g., NASDAQ, NYSE

    @Column(length = 100)
    private String sector; // e.g., Technology, Healthcare

    @Column(precision = 5, scale = 2)
    private BigDecimal dividendYield; // e.g., 2.50 for 2.5%

    @Column
    private Boolean fractionalAllowed; // Can trade fractional shares?

    @Override
    public AssetType getType() {
        return AssetType.STOCK;
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
        return "Stock - Equity shares in " + (sector != null ? sector : "various") + " sector";
    }

    @Override
    public boolean isQuantityValid(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        // If fractional not allowed, quantity must be whole number
        if (Boolean.FALSE.equals(fractionalAllowed)) {
            return quantity.stripTrailingZeros().scale() <= 0;
        }
        return true;
    }

    @Override
    public BigDecimal getMinimumQuantityIncrement() {
        return Boolean.TRUE.equals(fractionalAllowed) ? new BigDecimal("0.000001") : BigDecimal.ONE;
    }
}
