package com.demo.MoneyMap.mapper;

import com.demo.MoneyMap.dto.request.PortfolioRequestDTO;
import com.demo.MoneyMap.dto.response.AssetSummaryDTO;
import com.demo.MoneyMap.dto.response.PortfolioResponseDTO;
import com.demo.MoneyMap.entity.Asset;
import com.demo.MoneyMap.entity.Portfolio;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for Portfolio entity and DTOs.
 * Handles conversions between Portfolio entity and its DTOs.
 */
@Component
public class PortfolioMapper {

    /**
     * Convert PortfolioRequestDTO to Portfolio entity.
     */
    public Portfolio toEntity(PortfolioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Portfolio.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .active(true)
                .build();
    }

    /**
     * Convert Portfolio entity to PortfolioResponseDTO (without assets).
     */
    public PortfolioResponseDTO toResponseDTO(Portfolio entity) {
        if (entity == null) {
            return null;
        }

        return PortfolioResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .clientId(entity.getClient() != null ? entity.getClient().getId() : null)
                .clientName(entity.getClient() != null ? entity.getClient().getFullName() : null)
                .totalValue(entity.getTotalValue())
                .active(entity.getActive())
                .assetCount(entity.getAssets() != null ? entity.getAssets().size() : 0)
                .assets(Collections.emptyList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert Portfolio entity to PortfolioResponseDTO (with assets).
     */
    public PortfolioResponseDTO toResponseDTOWithAssets(Portfolio entity) {
        if (entity == null) {
            return null;
        }

        List<AssetSummaryDTO> assetSummaries = entity.getAssets() != null
                ? entity.getAssets().stream()
                        .map(this::toAssetSummaryDTO)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return PortfolioResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .clientId(entity.getClient() != null ? entity.getClient().getId() : null)
                .clientName(entity.getClient() != null ? entity.getClient().getFullName() : null)
                .totalValue(entity.getTotalValue())
                .active(entity.getActive())
                .assetCount(assetSummaries.size())
                .assets(assetSummaries)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Update existing Portfolio entity from PortfolioRequestDTO.
     */
    public void updateEntityFromDTO(PortfolioRequestDTO dto, Portfolio entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
    }

    /**
     * Convert Asset to AssetSummaryDTO.
     */
    private AssetSummaryDTO toAssetSummaryDTO(Asset asset) {
        return AssetSummaryDTO.builder()
                .id(asset.getId())
                .name(asset.getName())
                .symbol(asset.getSymbol())
                .assetType(asset.getType())
                .currentValue(asset.getCurrentValue())
                .quantity(asset.getQuantity())
                .build();
    }
}
