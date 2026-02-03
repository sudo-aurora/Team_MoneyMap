package com.demo.MoneyMap.repository;

import com.demo.MoneyMap.entity.Alert;
import com.demo.MoneyMap.entity.enums.AlertSeverity;
import com.demo.MoneyMap.entity.enums.AlertStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Alert entity operations.
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    /**
     * Find alert by reference.
     */
    Optional<Alert> findByAlertReference(String alertReference);

    /**
     * Find alerts by status.
     */
    List<Alert> findByStatus(AlertStatus status);

    /**
     * Find alerts by status with pagination.
     */
    Page<Alert> findByStatus(AlertStatus status, Pageable pageable);

    /**
     * Find alerts by severity.
     */
    List<Alert> findBySeverity(AlertSeverity severity);

    /**
     * Find alerts by severity with pagination.
     */
    Page<Alert> findBySeverity(AlertSeverity severity, Pageable pageable);

    /**
     * Find alerts by status and severity.
     */
    Page<Alert> findByStatusAndSeverity(AlertStatus status, AlertSeverity severity, Pageable pageable);

    /**
     * Find alerts by rule ID.
     */
    List<Alert> findByRuleId(Long ruleId);

    /**
     * Find alerts by rule ID with pagination.
     */
    Page<Alert> findByRuleId(Long ruleId, Pageable pageable);

    /**
     * Find alerts by account ID.
     */
    List<Alert> findByAccountId(String accountId);

    /**
     * Find alerts by account ID with pagination.
     */
    Page<Alert> findByAccountId(String accountId, Pageable pageable);

    /**
     * Find alerts created within date range.
     */
    Page<Alert> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find alert with triggering payments eagerly loaded.
     */
    @Query("SELECT a FROM Alert a LEFT JOIN FETCH a.triggeringPayments LEFT JOIN FETCH a.rule WHERE a.id = :id")
    Optional<Alert> findByIdWithDetails(@Param("id") Long id);

    /**
     * Count alerts by status.
     */
    long countByStatus(AlertStatus status);

    /**
     * Count alerts by severity.
     */
    long countBySeverity(AlertSeverity severity);

    /**
     * Count open alerts by severity.
     */
    long countByStatusAndSeverity(AlertStatus status, AlertSeverity severity);

    /**
     * Get average time to acknowledge alerts (in seconds).
     */
    @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, a.createdAt, a.acknowledgedAt)) FROM Alert a WHERE a.acknowledgedAt IS NOT NULL")
    Double getAverageAcknowledgeTime();

    /**
     * Find open alerts ordered by severity and created date.
     */
    @Query("SELECT a FROM Alert a WHERE a.status = 'OPEN' ORDER BY a.severity ASC, a.createdAt ASC")
    List<Alert> findOpenAlertsPrioritized(Pageable pageable);

    /**
     * Count alerts created today.
     */
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.createdAt >= :startOfDay")
    long countAlertsCreatedSince(@Param("startOfDay") LocalDateTime startOfDay);
}
