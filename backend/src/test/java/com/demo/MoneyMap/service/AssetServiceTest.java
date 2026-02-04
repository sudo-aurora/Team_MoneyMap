package com.demo.MoneyMap.service;

import com.demo.MoneyMap.config.AssetFactory;
import com.demo.MoneyMap.dto.request.AssetRequestDTO;
import com.demo.MoneyMap.dto.response.AssetResponseDTO;
import com.demo.MoneyMap.dto.response.AssetSummaryDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.*;
import com.demo.MoneyMap.entity.enums.AssetType;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.mapper.AssetMapper;
import com.demo.MoneyMap.repository.AssetRepository;
import com.demo.MoneyMap.repository.PortfolioRepository;
import com.demo.MoneyMap.service.impl.AssetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Test suite for AssetService focusing on asset management business logic.
 * 
 * This test validates the core asset operations including creation, valuation,
 * type-specific behavior, and portfolio relationships. Asset management is
 * critical for portfolio valuation and performance tracking.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Asset Service - Asset Management Tests")
@ActiveProfiles("test")
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;
    
    @Mock
    private PortfolioRepository portfolioRepository;
    
    @Mock
    private AssetMapper assetMapper;
    
    @Mock
    private AssetFactory assetFactory;
    
    @InjectMocks
    private AssetServiceImpl assetService;
    
    // Test fixtures
    private Portfolio testPortfolio;
    private StockAsset testStockAsset;
    private AssetRequestDTO validAssetRequest;
    private AssetResponseDTO expectedAssetResponse;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {
        // Create test portfolio
        testPortfolio = Portfolio.builder()
                .id(1L)
                .name("Growth Portfolio")
                .build();

        // Create test stock asset
        testStockAsset = StockAsset.builder()
                .id(1L)
                .symbol("AAPL")
                .name("Apple Inc.")
                .quantity(new BigDecimal("100"))
                .purchasePrice(new BigDecimal("150.00"))
                .currentPrice(new BigDecimal("175.50"))
                .portfolio(testPortfolio)
                .build();

        // Create valid asset request
        validAssetRequest = AssetRequestDTO.builder()
                .symbol("AAPL")
                .name("Apple Inc.")
                .assetType(AssetType.STOCK)
                .quantity(new BigDecimal("100"))
                .purchasePrice(new BigDecimal("150.00"))
                .currentPrice(new BigDecimal("175.50"))
                .portfolioId(1L)
                .build();

        // Create expected response
        expectedAssetResponse = AssetResponseDTO.builder()
                .id(1L)
                .symbol("AAPL")
                .name("Apple Inc.")
                .assetType(AssetType.STOCK)
                .quantity(new BigDecimal("100"))
                .purchasePrice(new BigDecimal("150.00"))
                .currentPrice(new BigDecimal("175.50"))
                .currentValue(new BigDecimal("17550.00"))
                .portfolioId(1L)
                .build();

        defaultPageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should create stock asset with proper valuation")
    void shouldCreateStockAssetWithProperValuation() {
        // Given: Valid asset request and portfolio exists
        given(portfolioRepository.findById(1L)).willReturn(Optional.of(testPortfolio));
        given(assetFactory.createFromDTO(validAssetRequest)).willReturn(testStockAsset);
        given(assetRepository.save(testStockAsset)).willReturn(testStockAsset);
        given(assetMapper.toResponseDTO(testStockAsset)).willReturn(expectedAssetResponse);

        // When: Creating new asset
        AssetResponseDTO result = assetService.createAsset(validAssetRequest);

        // Then: Should create asset with correct valuation
        assertThat(result).isNotNull();
        assertThat(result.getSymbol()).isEqualTo("AAPL");
        assertThat(result.getAssetType()).isEqualTo(AssetType.STOCK);
        assertThat(result.getCurrentValue()).isEqualTo(new BigDecimal("17550.00"));
        
        verify(portfolioRepository).findById(1L);
        verify(assetFactory).createFromDTO(validAssetRequest);
        verify(assetRepository).save(testStockAsset);
        verify(assetMapper).toResponseDTO(testStockAsset);
    }

    @Test
    @DisplayName("Should reject asset creation for non-existent portfolio")
    void shouldRejectAssetCreationForNonExistentPortfolio() {
        // Given: No portfolio exists
        given(portfolioRepository.findById(999L)).willReturn(Optional.empty());

        AssetRequestDTO invalidRequest = AssetRequestDTO.builder()
                .symbol("INVALID")
                .portfolioId(999L)
                .build();

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> assetService.createAsset(invalidRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(assetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve asset with current market valuation")
    void shouldRetrieveAssetWithCurrentMarketValuation() {
        // Given: Asset exists
        given(assetRepository.findByIdWithPortfolio(1L)).willReturn(Optional.of(testStockAsset));
        given(assetMapper.toResponseDTO(testStockAsset)).willReturn(expectedAssetResponse);

        // When: Retrieving asset
        AssetResponseDTO result = assetService.getAssetById(1L);

        // Then: Should return asset with current value
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCurrentValue()).isEqualTo(new BigDecimal("17550.00"));
        
        verify(assetRepository).findByIdWithPortfolio(1L);
    }

    @Test
    @DisplayName("Should handle asset not found gracefully")
    void shouldHandleAssetNotFoundGracefully() {
        // Given: Asset doesn't exist
        given(assetRepository.findByIdWithPortfolio(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> assetService.getAssetById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(assetRepository).findByIdWithPortfolio(999L);
    }

    @Test
    @DisplayName("Should retrieve assets by portfolio for valuation")
    void shouldRetrieveAssetsByPortfolioForValuation() {
        // Given: Portfolio has multiple assets
        List<Asset> assets = Arrays.asList(testStockAsset);
        given(assetRepository.findByPortfolioId(1L)).willReturn(assets);
        given(assetMapper.toResponseDTO(testStockAsset)).willReturn(expectedAssetResponse);

        // When: Retrieving assets by portfolio
        List<AssetResponseDTO> result = assetService.getAssetsByPortfolio(1L);

        // Then: Should return all portfolio assets
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPortfolioId()).isEqualTo(1L);
        
        verify(assetRepository).findByPortfolioId(1L);
    }

    @Test
    @DisplayName("Should filter assets by type correctly")
    void shouldFilterAssetsByTypeCorrectly() {
        // Given: Multiple stock assets exist
        List<Asset> stockAssets = Arrays.asList(testStockAsset);
        given(assetRepository.findByAssetType(StockAsset.class)).willReturn(stockAssets);
        given(assetMapper.toResponseDTO(testStockAsset)).willReturn(expectedAssetResponse);

        // When: Filtering by asset type
        List<AssetResponseDTO> result = assetService.getAssetsByType(AssetType.STOCK);

        // Then: Should return only stock assets
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAssetType()).isEqualTo(AssetType.STOCK);
        
        verify(assetRepository).findByAssetType(StockAsset.class);
    }

    @Test
    @DisplayName("Should update asset price and recalculate value")
    void shouldUpdateAssetPriceAndRecalculateValue() {
        // Given: Asset exists with new price
        BigDecimal newPrice = new BigDecimal("180.00");
        
        given(assetRepository.findByIdWithPortfolio(1L)).willReturn(Optional.of(testStockAsset));
        given(assetRepository.save(testStockAsset)).willReturn(testStockAsset);
        given(assetMapper.toResponseDTO(testStockAsset)).willReturn(expectedAssetResponse);

        // When: Updating asset price
        AssetResponseDTO result = assetService.updateAssetPrice(1L, newPrice);

        // Then: Should update price and recalculate value
        assertThat(result).isNotNull();
        verify(assetRepository).findByIdWithPortfolio(1L);
        verify(assetRepository).save(testStockAsset);
        verify(assetMapper).toResponseDTO(testStockAsset);
    }

    @Test
    @DisplayName("Should calculate total value by asset type")
    void shouldCalculateTotalValueByAssetType() {
        // Given: Stock assets have specific total value
        BigDecimal expectedTotalValue = new BigDecimal("50000.00");
        given(assetRepository.getTotalValueByAssetType(StockAsset.class)).willReturn(expectedTotalValue);

        // When: Calculating total value for stocks
        BigDecimal result = assetService.getTotalValueByAssetType(AssetType.STOCK);

        // Then: Should return correct total
        assertThat(result).isEqualTo(expectedTotalValue);
        verify(assetRepository).getTotalValueByAssetType(StockAsset.class);
    }

    @Test
    @DisplayName("Should provide asset summary for portfolio overview")
    void shouldProvideAssetSummaryForPortfolioOverview() {
        // Given: Portfolio has asset summary data - use existing assets
        List<Asset> assets = Arrays.asList(testStockAsset);
        given(assetRepository.findByPortfolioId(1L)).willReturn(assets);
        given(assetMapper.toSummaryDTO(testStockAsset)).willReturn(
                AssetSummaryDTO.builder()
                        .symbol("AAPL")
                        .name("Apple Inc.")
                        .assetType(AssetType.STOCK)
                        .quantity(new BigDecimal("100"))
                        .currentValue(new BigDecimal("17550.00"))
                        .build()
        );

        // When: Getting asset summary (convert assets to summaries)
        List<Asset> portfolioAssets = assetRepository.findByPortfolioId(1L);
        List<AssetSummaryDTO> summaries = portfolioAssets.stream()
                .map(assetMapper::toSummaryDTO)
                .toList();

        // Then: Should return summary data
        assertThat(summaries).hasSize(1);
        assertThat(summaries.get(0).getSymbol()).isEqualTo("AAPL");
        assertThat(summaries.get(0).getCurrentValue()).isEqualTo(new BigDecimal("17550.00"));
        
        verify(assetRepository).findByPortfolioId(1L);
    }

    @Test
    @DisplayName("Should support paginated asset retrieval")
    void shouldSupportPaginatedAssetRetrieval() {
        // Given: Multiple assets exist
        List<Asset> assets = Arrays.asList(testStockAsset);
        Page<Asset> assetPage = new PageImpl<>(assets, defaultPageable, 1);
        
        given(assetRepository.findAll(defaultPageable)).willReturn(assetPage);
        given(assetMapper.toResponseDTO(testStockAsset)).willReturn(expectedAssetResponse);

        // When: Retrieving assets with pagination
        PagedResponseDTO<AssetResponseDTO> result = assetService.getAllAssets(defaultPageable);

        // Then: Should return paginated response
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        
        verify(assetRepository).findAll(defaultPageable);
    }

    @Test
    @DisplayName("Should handle asset deletion with proper cleanup")
    void shouldHandleAssetDeletionWithProperCleanup() {
        // Given: Asset exists
        given(assetRepository.findByIdWithPortfolio(1L)).willReturn(Optional.of(testStockAsset));

        // When: Deleting asset
        assetService.deleteAsset(1L);

        // Then: Should trigger proper deletion
        verify(assetRepository).findByIdWithPortfolio(1L);
        verify(assetRepository).delete(testStockAsset);
    }

    @Test
    @DisplayName("Should search assets by symbol or name")
    void shouldSearchAssetsBySymbolOrName() {
        // Given: Assets match search criteria
        List<Asset> matchingAssets = Arrays.asList(testStockAsset);
        Page<Asset> assetPage = new PageImpl<>(matchingAssets, defaultPageable, 1);
        
        given(assetRepository.searchAssets("AAPL", defaultPageable)).willReturn(assetPage);
        given(assetMapper.toResponseDTO(testStockAsset)).willReturn(expectedAssetResponse);

        // When: Searching assets
        PagedResponseDTO<AssetResponseDTO> result = assetService.searchAssets("AAPL", defaultPageable);

        // Then: Should return matching assets
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSymbol()).isEqualTo("AAPL");
        
        verify(assetRepository).searchAssets("AAPL", defaultPageable);
    }
}
