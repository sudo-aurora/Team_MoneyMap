package com.demo.MoneyMap.entity;

import com.demo.MoneyMap.entity.enums.AssetType;
import com.demo.MoneyMap.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Abstract base entity for all asset types using Single Table Inheritance.
 * Demonstrates Inheritance and Polymorphism OOP concepts.
 */
@Entity
@Table(name = "assets")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "asset_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal currentValue;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "last_alert_sent_at")
    private Instant lastAlertSentAt;



    /**
     * Abstract method to get the asset type.
     * Each subclass must implement this.
     */
    public abstract AssetType getType();

    /**
     * Get allowed transaction types for this asset type.
     * Each subclass can override to customize.
     */
    public abstract Set<TransactionType> getAllowedTransactionTypes();

    /**
     * Get human-readable description of this asset type.
     */
    public abstract String getTypeDescription();

    /**
     * Validate if a quantity is valid for this asset type.
     * E.g., stocks may require whole numbers, crypto allows fractions.
     */
    public abstract boolean isQuantityValid(BigDecimal quantity);

    /**
     * Get minimum quantity increment for this asset type.
     * E.g., 1 for stocks, 0.00000001 for crypto.
     */
    public abstract BigDecimal getMinimumQuantityIncrement();

    /**
     * Calculate current value based on quantity and current price.
     */
    @PrePersist
    @PreUpdate
    public void calculateCurrentValue() {
        if (quantity != null && currentPrice != null) {
            this.currentValue = quantity.multiply(currentPrice);
        }
    }

    /**
     * Update current price and recalculate value.
     */
    public void updatePrice(BigDecimal newPrice) {
        this.currentPrice = newPrice;
        calculateCurrentValue();
        if (portfolio != null) {
            portfolio.recalculateTotalValue();
        }
    }

    /**
     * Add quantity (for BUY transactions).
     */
    public void addQuantity(BigDecimal amount) {
        this.quantity = this.quantity.add(amount);
        calculateCurrentValue();
    }

    /**
     * Subtract quantity (for SELL transactions).
     */
    public void subtractQuantity(BigDecimal amount) {
        this.quantity = this.quantity.subtract(amount);
        calculateCurrentValue();
    }

    /**
     * Calculate profit/loss.
     */
    public BigDecimal getProfitLoss() {
        BigDecimal costBasis = quantity.multiply(purchasePrice);
        return currentValue.subtract(costBasis);
    }

    /**
     * Calculate profit/loss percentage.
     */
    public BigDecimal getProfitLossPercentage() {
        BigDecimal costBasis = quantity.multiply(purchasePrice);
        if (costBasis.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getProfitLoss()
                .divide(costBasis, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    public boolean canSendAlert(Duration cooldown) {
        return lastAlertSentAt == null ||
                lastAlertSentAt.isBefore(Instant.now().minus(cooldown));
    }

    public void markAlertSent() {
        this.lastAlertSentAt = Instant.now();
    }

}
