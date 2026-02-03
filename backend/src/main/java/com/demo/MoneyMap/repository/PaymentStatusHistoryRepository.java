package com.demo.MoneyMap.repository;

import com.demo.MoneyMap.entity.PaymentStatusHistory;
import com.demo.MoneyMap.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for PaymentStatusHistory entity operations.
 */
@Repository
public interface PaymentStatusHistoryRepository extends JpaRepository<PaymentStatusHistory, Long> {

    /**
     * Find all history entries for a payment.
     */
    List<PaymentStatusHistory> findByPaymentIdOrderByTimestampDesc(Long paymentId);

    /**
     * Find history entries by status.
     */
    List<PaymentStatusHistory> findByStatus(PaymentStatus status);

    /**
     * Count history entries for a payment.
     */
    long countByPaymentId(Long paymentId);
}
