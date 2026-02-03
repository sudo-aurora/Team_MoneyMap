package com.demo.MoneyMap.repository;

import com.demo.MoneyMap.entity.Asset;
import com.demo.MoneyMap.entity.enums.AssetType;
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
 * Repository interface for Asset entity operations.
 * Provides CRUD operations and custom queries for asset management.
 */
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    /**
     * Find all assets in a specific portfolio.
     */
    List<Asset> findByPortfolioId(Long portfolioId);

    /**
     * Find all assets in a specific portfolio with pagination.
     */
    Page<Asset> findByPortfolioId(Long portfolioId, Pageable pageable);

    /**
     * Find all assets of a specific type.
     */
    @Query("SELECT a FROM Asset a WHERE TYPE(a) = :assetClass")
    List<Asset> findByAssetType(@Param("assetClass") Class<? extends Asset> assetClass);

    /**
     * Find all assets of a specific type with pagination.
     */
    @Query("SELECT a FROM Asset a WHERE TYPE(a) = :assetClass")
    Page<Asset> findByAssetType(@Param("assetClass") Class<? extends Asset> assetClass, Pageable pageable);

    /**
     * Find all assets in a portfolio by asset type.
     */
    @Query("SELECT a FROM Asset a WHERE a.portfolio.id = :portfolioId AND TYPE(a) = :assetClass")
    List<Asset> findByPortfolioIdAndAssetType(@Param("portfolioId") Long portfolioId, @Param("assetClass") Class<? extends Asset> assetClass);

    /**
     * Find asset by symbol.
     */
    List<Asset> findBySymbolIgnoreCase(String symbol);

    /**
     * Find asset with portfolio eagerly loaded.
     */
    @Query("SELECT a FROM Asset a JOIN FETCH a.portfolio WHERE a.id = :id")
    Optional<Asset> findByIdWithPortfolio(@Param("id") Long id);

    /**
     * Find asset with transactions eagerly loaded.
     */
    @Query("SELECT a FROM Asset a LEFT JOIN FETCH a.transactions WHERE a.id = :id")
    Optional<Asset> findByIdWithTransactions(@Param("id") Long id);

    /**
     * Search assets by name or symbol.
     */
    @Query("SELECT a FROM Asset a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.symbol) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Asset> searchAssets(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Get total value of all assets in a portfolio.
     */
    @Query("SELECT COALESCE(SUM(a.currentValue), 0) FROM Asset a WHERE a.portfolio.id = :portfolioId")
    BigDecimal getTotalValueByPortfolioId(@Param("portfolioId") Long portfolioId);

    /**
     * Get total value by asset type across all portfolios.
     */
    @Query("SELECT COALESCE(SUM(a.currentValue), 0) FROM Asset a WHERE TYPE(a) = :assetClass")
    BigDecimal getTotalValueByAssetType(@Param("assetClass") Class<? extends Asset> assetClass);

    /**
     * Count assets by portfolio ID.
     */
    long countByPortfolioId(Long portfolioId);

    /**
     * Count assets by asset type.
     */
    @Query("SELECT COUNT(a) FROM Asset a WHERE TYPE(a) = :assetClass")
    long countByAssetType(@Param("assetClass") Class<? extends Asset> assetClass);

    /**
     * Find all assets for a client (across all portfolios).
     */
    @Query("SELECT a FROM Asset a WHERE a.portfolio.client.id = :clientId")
    List<Asset> findByClientId(@Param("clientId") Long clientId);

    /**
     * Find all assets for a client with pagination.
     */
    @Query("SELECT a FROM Asset a WHERE a.portfolio.client.id = :clientId")
    Page<Asset> findByClientId(@Param("clientId") Long clientId, Pageable pageable);
}
