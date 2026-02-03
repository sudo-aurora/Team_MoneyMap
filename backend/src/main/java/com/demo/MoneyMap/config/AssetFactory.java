package com.demo.MoneyMap.config;

import com.demo.MoneyMap.dto.request.AssetRequestDTO;
import com.demo.MoneyMap.entity.*;
import com.demo.MoneyMap.entity.enums.AssetType;
import org.springframework.stereotype.Component;

/**
 * Factory Pattern implementation for creating Asset objects.
 * Creates the correct Asset subclass based on AssetType.
 * Demonstrates Factory Pattern and Polymorphism OOP concepts.
 */
@Component
public class AssetFactory {

    /**
     * Create the appropriate Asset subtype based on the assetType in the DTO.
     * This method demonstrates the Factory Pattern by encapsulating object creation logic.
     *
     * @param dto The asset request DTO containing asset type and fields
     * @return The appropriate Asset subclass (StockAsset, CryptoAsset, etc.)
     */
    public Asset createFromDTO(AssetRequestDTO dto) {
        Asset asset = switch (dto.getAssetType()) {
            case STOCK -> createStockAsset(dto);
            case CRYPTO -> createCryptoAsset(dto);
            case GOLD -> createGoldAsset(dto);
            case MUTUAL_FUND -> createMutualFundAsset(dto);
        };

        // Set common fields
        asset.setName(dto.getName());
        asset.setSymbol(dto.getSymbol());
        asset.setQuantity(dto.getQuantity());
        asset.setPurchasePrice(dto.getPurchasePrice());
        asset.setCurrentPrice(dto.getCurrentPrice());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setNotes(dto.getNotes());

        return asset;
    }

    private StockAsset createStockAsset(AssetRequestDTO dto) {
        return StockAsset.builder()
                .exchange(dto.getExchange())
                .sector(dto.getSector())
                .dividendYield(dto.getDividendYield())
                .fractionalAllowed(dto.getFractionalAllowed())
                .build();
    }

    private CryptoAsset createCryptoAsset(AssetRequestDTO dto) {
        return CryptoAsset.builder()
                .blockchain(dto.getBlockchain())
                .walletAddress(dto.getWalletAddress())
                .stakingEnabled(dto.getStakingEnabled())
                .stakingAPY(dto.getStakingAPY())
                .build();
    }

    private GoldAsset createGoldAsset(AssetRequestDTO dto) {
        return GoldAsset.builder()
                .purity(dto.getPurity())
                .weightInGrams(dto.getWeightInGrams())
                .storageLocation(dto.getStorageLocation())
                .certificateNumber(dto.getCertificateNumber())
                .isPhysical(dto.getIsPhysical())
                .build();
    }

    private MutualFundAsset createMutualFundAsset(AssetRequestDTO dto) {
        return MutualFundAsset.builder()
                .fundManager(dto.getFundManager())
                .expenseRatio(dto.getExpenseRatio())
                .navPrice(dto.getNavPrice())
                .minimumInvestment(dto.getMinimumInvestment())
                .riskLevel(dto.getRiskLevel())
                .build();
    }
}
