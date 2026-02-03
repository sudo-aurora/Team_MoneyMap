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
 * Cryptocurrency asset subclass with crypto-specific fields.
 * Demonstrates Inheritance OOP concept.
 */
@Entity
@DiscriminatorValue("CRYPTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CryptoAsset extends Asset {

    @Column(length = 50)
    private String blockchain; // e.g., Bitcoin, Ethereum, Binance Smart Chain

    @Column(length = 200)
    private String walletAddress; // Crypto wallet address

    @Column
    private Boolean stakingEnabled; // Can this crypto be staked?

    @Column(precision = 5, scale = 2)
    private BigDecimal stakingAPY; // Annual Percentage Yield for staking

    @Override
    public AssetType getType() {
        return AssetType.CRYPTO;
    }

    @Override
    public Set<TransactionType> getAllowedTransactionTypes() {
        return Set.of(
                TransactionType.BUY,
                TransactionType.SELL,
                TransactionType.TRANSFER_IN,
                TransactionType.TRANSFER_OUT,
                TransactionType.INTEREST // Staking rewards
        );
    }

    @Override
    public String getTypeDescription() {
        return "Cryptocurrency on " + (blockchain != null ? blockchain : "blockchain") +
                (Boolean.TRUE.equals(stakingEnabled) ? " (Staking enabled)" : "");
    }

    @Override
    public boolean isQuantityValid(BigDecimal quantity) {
        // Crypto allows very small fractions (e.g., Satoshis for Bitcoin)
        return quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public BigDecimal getMinimumQuantityIncrement() {
        return new BigDecimal("0.00000001"); // Satoshi-level precision
    }
}
