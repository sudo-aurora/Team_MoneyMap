package com.demo.MoneyMap.entity;

import com.demo.MoneyMap.entity.enums.AlertSeverity;
import com.demo.MoneyMap.entity.enums.RuleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a monitoring rule for transaction alerts.
 * Rules are evaluated against transactions to generate alerts.
 */
@Entity
@Table(name = "monitoring_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoringRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false)
    private RuleType ruleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AlertSeverity severity = AlertSeverity.MEDIUM;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(length = 500)
    private String description;

    // Rule parameters - stored as individual columns for type safety
    // Amount Threshold Rule parameters
    @Column(name = "threshold_amount", precision = 19, scale = 4)
    private BigDecimal thresholdAmount;

    @Column(name = "threshold_currency", length = 3)
    private String thresholdCurrency;

    // Velocity Rule parameters
    @Column(name = "max_transactions")
    private Integer maxTransactions;

    @Column(name = "time_window_minutes")
    private Integer timeWindowMinutes;

    // Daily Limit Rule parameters
    @Column(name = "daily_limit_amount", precision = 19, scale = 4)
    private BigDecimal dailyLimitAmount;

    // New Payee Rule parameters
    @Column(name = "lookback_days")
    private Integer lookbackDays;

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Alert> alerts = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
