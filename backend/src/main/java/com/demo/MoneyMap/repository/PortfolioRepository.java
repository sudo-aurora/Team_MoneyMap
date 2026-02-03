package com.demo.MoneyMap.repository;

import com.demo.MoneyMap.entity.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Portfolio entity operations.
 * Provides CRUD operations and custom queries for portfolio management.
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    /**
     * Find all portfolios for a specific client.
     */
    List<Portfolio> findByClientId(Long clientId);

    /**
     * Find all portfolios for a specific client with pagination.
     */
    Page<Portfolio> findByClientId(Long clientId, Pageable pageable);

    /**
     * Find all active portfolios for a specific client.
     */
    List<Portfolio> findByClientIdAndActiveTrue(Long clientId);

    /**
     * Find all active portfolios.
     */
    List<Portfolio> findByActiveTrue();

    /**
     * Find all active portfolios with pagination.
     */
    Page<Portfolio> findByActiveTrue(Pageable pageable);

    /**
     * Find portfolio with its assets eagerly loaded.
     */
    @Query("SELECT p FROM Portfolio p LEFT JOIN FETCH p.assets WHERE p.id = :id")
    Optional<Portfolio> findByIdWithAssets(@Param("id") Long id);

    /**
     * Find portfolio with client eagerly loaded.
     */
    @Query("SELECT p FROM Portfolio p JOIN FETCH p.client WHERE p.id = :id")
    Optional<Portfolio> findByIdWithClient(@Param("id") Long id);

    /**
     * Search portfolios by name.
     */
    @Query("SELECT p FROM Portfolio p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Portfolio> searchPortfolios(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Get total value of all portfolios for a client.
     */
    @Query("SELECT COALESCE(SUM(p.totalValue), 0) FROM Portfolio p WHERE p.client.id = :clientId AND p.active = true")
    BigDecimal getTotalValueByClientId(@Param("clientId") Long clientId);

    /**
     * Count portfolios by client ID.
     */
    long countByClientId(Long clientId);

    /**
     * Check if a portfolio already exists for the given client (one-to-one enforcement).
     * CRITICAL: Added for one portfolio per client validation.
     */
    boolean existsByClientId(Long clientId);

    /**
     * Count active portfolios.
     */
    long countByActiveTrue();
}
