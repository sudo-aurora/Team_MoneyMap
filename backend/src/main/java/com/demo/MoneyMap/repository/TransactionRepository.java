package com.demo.MoneyMap.repository;

import com.demo.MoneyMap.entity.Transaction;
import com.demo.MoneyMap.entity.enums.TransactionType;
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
 * Repository interface for Transaction entity operations.
 * Provides CRUD operations and custom queries for transaction management.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find all transactions for a specific asset.
     */
    List<Transaction> findByAssetId(Long assetId);

    /**
     * Find all transactions for a specific asset with pagination.
     */
    Page<Transaction> findByAssetId(Long assetId, Pageable pageable);

    /**
     * Find all transactions of a specific type.
     */
    List<Transaction> findByTransactionType(TransactionType transactionType);

    /**
     * Find all transactions of a specific type with pagination.
     */
    Page<Transaction> findByTransactionType(TransactionType transactionType, Pageable pageable);

    /**
     * Find transactions within a date range.
     */
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find transactions within a date range with pagination.
     */
    Page<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find transaction with asset eagerly loaded.
     */
    @Query("SELECT t FROM Transaction t JOIN FETCH t.asset WHERE t.id = :id")
    Optional<Transaction> findByIdWithAsset(@Param("id") Long id);

    /**
     * Find all transactions for a portfolio.
     */
    @Query("SELECT t FROM Transaction t WHERE t.asset.portfolio.id = :portfolioId ORDER BY t.transactionDate DESC")
    List<Transaction> findByPortfolioId(@Param("portfolioId") Long portfolioId);

    /**
     * Find all transactions for a portfolio with pagination.
     */
    @Query("SELECT t FROM Transaction t WHERE t.asset.portfolio.id = :portfolioId")
    Page<Transaction> findByPortfolioId(@Param("portfolioId") Long portfolioId, Pageable pageable);

    /**
     * Find all transactions for a client.
     */
    @Query("SELECT t FROM Transaction t WHERE t.asset.portfolio.client.id = :clientId ORDER BY t.transactionDate DESC")
    List<Transaction> findByClientId(@Param("clientId") Long clientId);

    /**
     * Find all transactions for a client with pagination.
     */
    @Query("SELECT t FROM Transaction t WHERE t.asset.portfolio.client.id = :clientId")
    Page<Transaction> findByClientId(@Param("clientId") Long clientId, Pageable pageable);

    /**
     * Get total transaction amount by type for an asset.
     */
    @Query("SELECT COALESCE(SUM(t.totalAmount), 0) FROM Transaction t WHERE t.asset.id = :assetId AND t.transactionType = :transactionType")
    BigDecimal getTotalAmountByAssetIdAndType(@Param("assetId") Long assetId, @Param("transactionType") TransactionType transactionType);

    /**
     * Count transactions by asset ID.
     */
    long countByAssetId(Long assetId);

    /**
     * Find recent transactions for an asset.
     */
    @Query("SELECT t FROM Transaction t WHERE t.asset.id = :assetId ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentByAssetId(@Param("assetId") Long assetId, Pageable pageable);
}
