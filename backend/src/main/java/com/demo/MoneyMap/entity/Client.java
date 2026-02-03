package com.demo.MoneyMap.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a client of the asset management firm.
 * Each client has exactly one portfolio (OneToOne relationship).
 * Supports multi-country clients with localization fields.
 */
@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String stateOrProvince;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 2)
    private String countryCode; // ISO 3166-1 alpha-2 (e.g., US, IN, GB, DE)

    @Column(length = 100)
    private String country; // Full country name (e.g., United States, India)

    @Column(length = 3)
    private String preferredCurrency; // ISO 4217 (e.g., USD, EUR, INR)

    @Column(length = 50)
    private String timezone; // IANA timezone (e.g., America/New_York, Asia/Kolkata)

    @Column(length = 10)
    private String locale; // Locale for language/formatting (e.g., en_US, hi_IN)

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // OneToOne relationship with Portfolio
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Portfolio portfolio;

    /**
     * Utility method to set the portfolio for the client.
     * Maintains bidirectional relationship.
     */
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
        if (portfolio != null) {
            portfolio.setClient(this);
        }
    }

    /**
     * Get full name of client.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
