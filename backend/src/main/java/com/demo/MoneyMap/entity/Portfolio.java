package com.demo.MoneyMap.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a portfolio belonging to a client.
 * Each portfolio belongs to exactly ONE client (OneToOne relationship).
 */
@Entity
@Table(name = "portfolios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Asset> assets = new ArrayList<>();

    /**
     * Add an asset to this portfolio.
     * Maintains bidirectional relationship.
     */
    public void addAsset(Asset asset) {
        assets.add(asset);
        asset.setPortfolio(this);
        recalculateTotalValue();
    }

    /**
     * Remove an asset from this portfolio.
     */
    public void removeAsset(Asset asset) {
        assets.remove(asset);
        asset.setPortfolio(null);
        recalculateTotalValue();
    }

    /**
     * Recalculate total portfolio value from all assets.
     */
    public void recalculateTotalValue() {
        this.totalValue = assets.stream()
                .map(Asset::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
