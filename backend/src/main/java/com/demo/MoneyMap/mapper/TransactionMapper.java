package com.demo.MoneyMap.mapper;

import com.demo.MoneyMap.dto.request.TransactionRequestDTO;
import com.demo.MoneyMap.dto.response.TransactionResponseDTO;
import com.demo.MoneyMap.entity.Transaction;
import org.springframework.stereotype.Component;

/**
 * Mapper class for Transaction entity and DTOs.
 * Handles conversions between Transaction entity and its DTOs.
 */
@Component
public class TransactionMapper {

    /**
     * Convert TransactionRequestDTO to Transaction entity.
     */
    public Transaction toEntity(TransactionRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Transaction.builder()
                .transactionType(dto.getTransactionType())
                .quantity(dto.getQuantity())
                .pricePerUnit(dto.getPricePerUnit())
                .fees(dto.getFees())
                .transactionDate(dto.getTransactionDate())
                .notes(dto.getNotes())
                .build();
    }

    /**
     * Convert Transaction entity to TransactionResponseDTO.
     */
    public TransactionResponseDTO toResponseDTO(Transaction entity) {
        if (entity == null) {
            return null;
        }

        return TransactionResponseDTO.builder()
                .id(entity.getId())
                .transactionType(entity.getTransactionType())
                .transactionTypeDisplayName(entity.getTransactionType() != null 
                        ? entity.getTransactionType().getDisplayName() : null)
                .quantity(entity.getQuantity())
                .pricePerUnit(entity.getPricePerUnit())
                .totalAmount(entity.getTotalAmount())
                .fees(entity.getFees())
                .assetId(entity.getAsset() != null ? entity.getAsset().getId() : null)
                .assetName(entity.getAsset() != null ? entity.getAsset().getName() : null)
                .assetSymbol(entity.getAsset() != null ? entity.getAsset().getSymbol() : null)
                .transactionDate(entity.getTransactionDate())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Update existing Transaction entity from TransactionRequestDTO.
     */
    public void updateEntityFromDTO(TransactionRequestDTO dto, Transaction entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTransactionType(dto.getTransactionType());
        entity.setQuantity(dto.getQuantity());
        entity.setPricePerUnit(dto.getPricePerUnit());
        entity.setFees(dto.getFees());
        entity.setTransactionDate(dto.getTransactionDate());
        entity.setNotes(dto.getNotes());
        entity.calculateTotalAmount();
    }
}
