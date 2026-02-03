package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.config.AssetFactory;
import com.demo.MoneyMap.dto.request.AssetRequestDTO;
import com.demo.MoneyMap.dto.response.AssetResponseDTO;
import com.demo.MoneyMap.dto.response.AssetSummaryDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.*;
import com.demo.MoneyMap.entity.enums.AssetType;
import com.demo.MoneyMap.exception.BadRequestException;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.mapper.AssetMapper;
import com.demo.MoneyMap.repository.AssetRepository;
import com.demo.MoneyMap.repository.PortfolioRepository;
import com.demo.MoneyMap.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AssetService.
 * Uses Factory Pattern for creating polymorphic Asset objects.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetMapper assetMapper;
    private final AssetFactory assetFactory;

    @Override
    public AssetResponseDTO createAsset(AssetRequestDTO requestDTO) {
        log.info("Creating new {} asset: {} for portfolio ID: {}",
                requestDTO.getAssetType(), requestDTO.getSymbol(), requestDTO.getPortfolioId());

        Portfolio portfolio = portfolioRepository.findById(requestDTO.getPortfolioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio not found with ID: " + requestDTO.getPortfolioId()));

        // Use Factory Pattern to create the correct Asset subtype
        Asset asset = assetFactory.createFromDTO(requestDTO);
        asset.setPortfolio(portfolio);

        // Validate quantity for this asset type
        if (!asset.isQuantityValid(requestDTO.getQuantity())) {
            throw new BadRequestException(
                    "Invalid quantity for " + asset.getType() + " asset. Minimum increment: " +
                            asset.getMinimumQuantityIncrement());
        }

        Asset savedAsset = assetRepository.save(asset);
        portfolio.recalculateTotalValue();
        portfolioRepository.save(portfolio);

        log.info("Successfully created asset with ID: {}", savedAsset.getId());
        return assetMapper.toResponseDTO(savedAsset);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponseDTO getAssetById(Long id) {
        log.debug("Fetching asset with ID: {}", id);
        Asset asset = assetRepository.findByIdWithPortfolio(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));
        return assetMapper.toResponseDTO(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponseDTO getAssetByIdWithTransactions(Long id) {
        log.debug("Fetching asset with transactions for ID: {}", id);
        Asset asset = assetRepository.findByIdWithTransactions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));
        return assetMapper.toResponseDTO(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponseDTO> getAllAssets() {
        log.debug("Fetching all assets");
        return assetRepository.findAll().stream()
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AssetResponseDTO> getAllAssets(Pageable pageable) {
        log.debug("Fetching all assets with pagination");
        Page<Asset> page = assetRepository.findAll(pageable);
        return PagedResponseDTO.from(page, assetMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponseDTO> getAssetsByPortfolio(Long portfolioId) {
        log.debug("Fetching assets for portfolio ID: {}", portfolioId);
        return assetRepository.findByPortfolioId(portfolioId).stream()
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponseDTO> getAssetsByPortfolioId(Long portfolioId) {
        return getAssetsByPortfolio(portfolioId);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AssetResponseDTO> getAssetsByPortfolio(Long portfolioId, Pageable pageable) {
        log.debug("Fetching assets for portfolio ID: {} with pagination", portfolioId);
        Page<Asset> page = assetRepository.findByPortfolioId(portfolioId, pageable);
        return PagedResponseDTO.from(page, assetMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AssetResponseDTO> getAssetsByPortfolioId(Long portfolioId, Pageable pageable) {
        return getAssetsByPortfolio(portfolioId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponseDTO> getAssetsByType(AssetType assetType) {
        log.debug("Fetching assets of type: {}", assetType);
        Class<? extends Asset> assetClass = getAssetClassForType(assetType);
        return assetRepository.findByAssetType(assetClass).stream()
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AssetResponseDTO> getAssetsByType(AssetType assetType, Pageable pageable) {
        log.debug("Fetching assets of type: {} with pagination", assetType);
        Class<? extends Asset> assetClass = getAssetClassForType(assetType);
        Page<Asset> page = assetRepository.findByAssetType(assetClass, pageable);
        return PagedResponseDTO.from(page, assetMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponseDTO> getAssetsByClient(Long clientId) {
        log.debug("Fetching assets for client ID: {}", clientId);
        return assetRepository.findByClientId(clientId).stream()
                .map(assetMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponseDTO> getAssetsByClientId(Long clientId) {
        return getAssetsByClient(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AssetResponseDTO> searchAssets(String query, Pageable pageable) {
        log.debug("Searching assets with query: {}", query);
        Page<Asset> page = assetRepository.searchAssets(query, pageable);
        return PagedResponseDTO.from(page, assetMapper::toResponseDTO);
    }

    @Override
    public AssetResponseDTO updateAsset(Long id, AssetRequestDTO requestDTO) {
        log.info("Updating asset with ID: {}", id);

        Asset asset = assetRepository.findByIdWithPortfolio(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));

        Portfolio portfolio = asset.getPortfolio();

        // If portfolio changed, fetch new one
        if (!portfolio.getId().equals(requestDTO.getPortfolioId())) {
            portfolio = portfolioRepository.findById(requestDTO.getPortfolioId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Portfolio not found with ID: " + requestDTO.getPortfolioId()));
            asset.setPortfolio(portfolio);
        }

        assetMapper.updateEntityFromDTO(requestDTO, asset);

        Asset updatedAsset = assetRepository.save(asset);
        portfolio.recalculateTotalValue();
        portfolioRepository.save(portfolio);

        log.info("Successfully updated asset with ID: {}", id);
        return assetMapper.toResponseDTO(updatedAsset);
    }

    @Override
    public AssetResponseDTO updateAssetPrice(Long id, BigDecimal currentPrice) {
        log.info("Updating price for asset ID: {} to {}", id, currentPrice);

        Asset asset = assetRepository.findByIdWithPortfolio(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));

        asset.updatePrice(currentPrice);
        Asset updatedAsset = assetRepository.save(asset);

        Portfolio portfolio = asset.getPortfolio();
        portfolio.recalculateTotalValue();
        portfolioRepository.save(portfolio);

        log.info("Successfully updated price for asset ID: {}", id);
        return assetMapper.toResponseDTO(updatedAsset);
    }

    @Override
    public void deleteAsset(Long id) {
        log.info("Deleting asset with ID: {}", id);

        Asset asset = assetRepository.findByIdWithPortfolio(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + id));

        Portfolio portfolio = asset.getPortfolio();
        assetRepository.delete(asset);

        portfolio.recalculateTotalValue();
        portfolioRepository.save(portfolio);

        log.info("Successfully deleted asset with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalValueByType(AssetType assetType) {
        log.debug("Calculating total value for asset type: {}", assetType);
        Class<? extends Asset> assetClass = getAssetClassForType(assetType);
        return assetRepository.getTotalValueByAssetType(assetClass);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalValueByAssetType(AssetType assetType) {
        return getTotalValueByType(assetType);
    }

    /**
     * Helper method to convert AssetType enum to Asset class.
     */
    private Class<? extends Asset> getAssetClassForType(AssetType assetType) {
        return switch (assetType) {
            case STOCK -> StockAsset.class;
            case CRYPTO -> CryptoAsset.class;
            case GOLD -> GoldAsset.class;
            case MUTUAL_FUND -> MutualFundAsset.class;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetSummaryDTO> getAssetSummaryByPortfolio(Long portfolioId) {
        log.debug("Getting asset summary for portfolio ID: {}", portfolioId);
        return assetRepository.findByPortfolioId(portfolioId).stream()
                .map(assetMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetType> getAssetTypes() {
        return Arrays.asList(AssetType.values());
    }
}
