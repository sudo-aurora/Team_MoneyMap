package com.demo.MoneyMap.mapper;

import com.demo.MoneyMap.dto.request.AssetRequestDTO;
import com.demo.MoneyMap.dto.response.AssetResponseDTO;
import com.demo.MoneyMap.dto.response.AssetSummaryDTO;
import com.demo.MoneyMap.entity.*;
import org.springframework.stereotype.Component;

/**
 * Mapper for Asset entity and DTOs.
 * Handles polymorphic conversion of Asset subclasses.
 */
@Component
public class AssetMapper {

    public AssetResponseDTO toResponseDTO(Asset entity) {
        if (entity == null) {
            return null;
        }

        AssetResponseDTO.AssetResponseDTOBuilder builder = AssetResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .symbol(entity.getSymbol())
                .assetType(entity.getType())
                .quantity(entity.getQuantity())
                .purchasePrice(entity.getPurchasePrice())
                .currentPrice(entity.getCurrentPrice())
                .currentValue(entity.getCurrentValue())
                .purchaseDate(entity.getPurchaseDate())
                .notes(entity.getNotes())
                .portfolioId(entity.getPortfolio() != null ? entity.getPortfolio().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .profitLoss(entity.getProfitLoss())
                .profitLossPercentage(entity.getProfitLossPercentage());

        // Add type-specific fields based on subclass
        if (entity instanceof StockAsset stock) {
            builder
                    .exchange(stock.getExchange())
                    .sector(stock.getSector())
                    .dividendYield(stock.getDividendYield())
                    .fractionalAllowed(stock.getFractionalAllowed());
        } else if (entity instanceof CryptoAsset crypto) {
            builder
                    .blockchain(crypto.getBlockchain())
                    .walletAddress(crypto.getWalletAddress())
                    .stakingEnabled(crypto.getStakingEnabled())
                    .stakingAPY(crypto.getStakingAPY());
        } else if (entity instanceof GoldAsset gold) {
            builder
                    .purity(gold.getPurity())
                    .weightInGrams(gold.getWeightInGrams())
                    .storageLocation(gold.getStorageLocation())
                    .certificateNumber(gold.getCertificateNumber())
                    .isPhysical(gold.getIsPhysical());
        } else if (entity instanceof MutualFundAsset fund) {
            builder
                    .fundManager(fund.getFundManager())
                    .expenseRatio(fund.getExpenseRatio())
                    .navPrice(fund.getNavPrice())
                    .minimumInvestment(fund.getMinimumInvestment())
                    .riskLevel(fund.getRiskLevel());
        }

        return builder.build();
    }

    public AssetSummaryDTO toSummaryDTO(Asset entity) {
        if (entity == null) {
            return null;
        }

        return AssetSummaryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .symbol(entity.getSymbol())
                .assetType(entity.getType())
                .quantity(entity.getQuantity())
                .currentPrice(entity.getCurrentPrice())
                .currentValue(entity.getCurrentValue())
                .profitLoss(entity.getProfitLoss())
                .profitLossPercentage(entity.getProfitLossPercentage())
                .build();
    }

    public void updateEntityFromDTO(AssetRequestDTO dto, Asset entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getSymbol() != null) {
            entity.setSymbol(dto.getSymbol());
        }
        if (dto.getQuantity() != null) {
            entity.setQuantity(dto.getQuantity());
        }
        if (dto.getPurchasePrice() != null) {
            entity.setPurchasePrice(dto.getPurchasePrice());
        }
        if (dto.getCurrentPrice() != null) {
            entity.setCurrentPrice(dto.getCurrentPrice());
        }
        if (dto.getPurchaseDate() != null) {
            entity.setPurchaseDate(dto.getPurchaseDate());
        }
        if (dto.getNotes() != null) {
            entity.setNotes(dto.getNotes());
        }

        // Update type-specific fields
        if (entity instanceof StockAsset stock) {
            if (dto.getExchange() != null) stock.setExchange(dto.getExchange());
            if (dto.getSector() != null) stock.setSector(dto.getSector());
            if (dto.getDividendYield() != null) stock.setDividendYield(dto.getDividendYield());
            if (dto.getFractionalAllowed() != null) stock.setFractionalAllowed(dto.getFractionalAllowed());
        } else if (entity instanceof CryptoAsset crypto) {
            if (dto.getBlockchain() != null) crypto.setBlockchain(dto.getBlockchain());
            if (dto.getWalletAddress() != null) crypto.setWalletAddress(dto.getWalletAddress());
            if (dto.getStakingEnabled() != null) crypto.setStakingEnabled(dto.getStakingEnabled());
            if (dto.getStakingAPY() != null) crypto.setStakingAPY(dto.getStakingAPY());
        } else if (entity instanceof GoldAsset gold) {
            if (dto.getPurity() != null) gold.setPurity(dto.getPurity());
            if (dto.getWeightInGrams() != null) gold.setWeightInGrams(dto.getWeightInGrams());
            if (dto.getStorageLocation() != null) gold.setStorageLocation(dto.getStorageLocation());
            if (dto.getCertificateNumber() != null) gold.setCertificateNumber(dto.getCertificateNumber());
            if (dto.getIsPhysical() != null) gold.setIsPhysical(dto.getIsPhysical());
        } else if (entity instanceof MutualFundAsset fund) {
            if (dto.getFundManager() != null) fund.setFundManager(dto.getFundManager());
            if (dto.getExpenseRatio() != null) fund.setExpenseRatio(dto.getExpenseRatio());
            if (dto.getNavPrice() != null) fund.setNavPrice(dto.getNavPrice());
            if (dto.getMinimumInvestment() != null) fund.setMinimumInvestment(dto.getMinimumInvestment());
            if (dto.getRiskLevel() != null) fund.setRiskLevel(dto.getRiskLevel());
        }

        entity.calculateCurrentValue();
    }
}
