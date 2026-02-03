package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.AssetRequestDTO;
import com.demo.MoneyMap.dto.response.AssetResponseDTO;
import com.demo.MoneyMap.dto.response.AssetSummaryDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.enums.AssetType;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Asset management operations.
 * Defines contract for asset business logic.
 */
public interface AssetService {

    AssetResponseDTO createAsset(AssetRequestDTO requestDTO);

    AssetResponseDTO getAssetById(Long id);

    AssetResponseDTO getAssetByIdWithTransactions(Long id);

    List<AssetResponseDTO> getAllAssets();

    PagedResponseDTO<AssetResponseDTO> getAllAssets(Pageable pageable);

    List<AssetResponseDTO> getAssetsByPortfolio(Long portfolioId);

    List<AssetResponseDTO> getAssetsByPortfolioId(Long portfolioId);

    PagedResponseDTO<AssetResponseDTO> getAssetsByPortfolio(Long portfolioId, Pageable pageable);

    PagedResponseDTO<AssetResponseDTO> getAssetsByPortfolioId(Long portfolioId, Pageable pageable);

    List<AssetResponseDTO> getAssetsByType(AssetType assetType);

    PagedResponseDTO<AssetResponseDTO> getAssetsByType(AssetType assetType, Pageable pageable);

    List<AssetResponseDTO> getAssetsByClient(Long clientId);

    List<AssetResponseDTO> getAssetsByClientId(Long clientId);

    PagedResponseDTO<AssetResponseDTO> searchAssets(String query, Pageable pageable);

    AssetResponseDTO updateAsset(Long id, AssetRequestDTO requestDTO);

    AssetResponseDTO updateAssetPrice(Long id, BigDecimal currentPrice);

    void deleteAsset(Long id);

    BigDecimal getTotalValueByType(AssetType assetType);

    BigDecimal getTotalValueByAssetType(AssetType assetType);

    List<AssetSummaryDTO> getAssetSummaryByPortfolio(Long portfolioId);

    List<AssetType> getAssetTypes();
}
