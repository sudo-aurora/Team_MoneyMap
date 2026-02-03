package com.demo.MoneyMap.repository;

import com.demo.MoneyMap.entity.Payment;
import com.demo.MoneyMap.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity operations.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by payment reference.
     */
    Optional<Payment> findByPaymentReference(String paymentReference);

    /**
     * Find payment by idempotency key.
     */
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    /**
     * Check if payment exists with idempotency key.
     */
    boolean existsByIdempotencyKey(String idempotencyKey);

    /**
     * Find payments by status.
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Find payments by status with pagination.
     */
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    /**
     * Find payments by source account.
     */
    List<Payment> findBySourceAccount(String sourceAccount);

    /**
     * Find payments by source account with pagination.
     */
    Page<Payment> findBySourceAccount(String sourceAccount, Pageable pageable);

    /**
     * Find payments by destination account.
     */
    List<Payment> findByDestinationAccount(String destinationAccount);

    /**
     * Find payments created within date range.
     */
    Page<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find payment with status history eagerly loaded.
     */
    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.statusHistory WHERE p.id = :id")
    Optional<Payment> findByIdWithHistory(@Param("id") Long id);

    /**
     * Count payments by status.
     */
    long countByStatus(PaymentStatus status);

    /**
     * Find payments exceeding amount threshold (for monitoring rules).
     */
    @Query("SELECT p FROM Payment p WHERE p.amount > :threshold AND p.createdAt > :since")
    List<Payment> findByAmountGreaterThanAndCreatedAfter(@Param("threshold") BigDecimal threshold, @Param("since") LocalDateTime since);

    /**
     * Count recent transactions by account (for velocity rule).
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.sourceAccount = :accountId AND p.createdAt > :since")
    long countRecentBySourceAccount(@Param("accountId") String accountId, @Param("since") LocalDateTime since);

    /**
     * Get total amount by account for a day (for daily limit rule).
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.sourceAccount = :accountId AND p.createdAt >= :dayStart AND p.createdAt < :dayEnd")
    BigDecimal sumAmountByAccountAndDate(@Param("accountId") String accountId, @Param("dayStart") LocalDateTime dayStart, @Param("dayEnd") LocalDateTime dayEnd);

    /**
     * Check if payee has been used before by account (for new payee rule).
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.sourceAccount = :sourceAccount AND p.destinationAccount = :destinationAccount AND p.createdAt < :before")
    long countPreviousPaymentToPayee(@Param("sourceAccount") String sourceAccount, @Param("destinationAccount") String destinationAccount, @Param("before") LocalDateTime before);

    /**
     * Search payments by reference or description.
     */
    @Query("SELECT p FROM Payment p WHERE LOWER(p.reference) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.paymentReference) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Payment> searchPayments(@Param("searchTerm") String searchTerm, Pageable pageable);
}
