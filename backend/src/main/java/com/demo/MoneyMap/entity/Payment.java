package com.demo.MoneyMap.entity;

import com.demo.MoneyMap.entity.enums.PaymentErrorCode;
import com.demo.MoneyMap.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a financial payment.
 * Tracks the complete lifecycle from creation through completion or failure.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_reference", unique = true, length = 50)
    private String paymentReference;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "source_account", nullable = false, length = 50)
    private String sourceAccount;

    @Column(name = "destination_account", nullable = false, length = 50)
    private String destinationAccount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_code")
    private PaymentErrorCode errorCode;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(length = 200)
    private String reference;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OrderBy("timestamp DESC")
    private List<PaymentStatusHistory> statusHistory = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Add a status history entry.
     */
    public void addStatusHistory(PaymentStatusHistory history) {
        statusHistory.add(history);
        history.setPayment(this);
    }

    /**
     * Generate a payment reference if not set.
     */
    @PrePersist
    public void generatePaymentReference() {
        if (paymentReference == null) {
            paymentReference = "PAY-" + System.currentTimeMillis();
        }
    }
}
