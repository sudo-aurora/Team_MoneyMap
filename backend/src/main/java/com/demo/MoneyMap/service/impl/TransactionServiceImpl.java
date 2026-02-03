package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.dto.request.TransactionRequestDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.dto.response.TransactionResponseDTO;
import com.demo.MoneyMap.entity.Asset;
import com.demo.MoneyMap.entity.Portfolio;
import com.demo.MoneyMap.entity.Transaction;
import com.demo.MoneyMap.entity.enums.TransactionType;
import com.demo.MoneyMap.exception.BadRequestException;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.mapper.TransactionMapper;
import com.demo.MoneyMap.repository.AssetRepository;
import com.demo.MoneyMap.repository.PortfolioRepository;
import com.demo.MoneyMap.repository.TransactionRepository;
import com.demo.MoneyMap.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TransactionService.
 * Includes type-specific transaction validation for polymorphic Asset types.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AssetRepository assetRepository;
    private final PortfolioRepository portfolioRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO) {
        log.info("Creating new {} transaction for asset ID: {}", requestDTO.getTransactionType(), requestDTO.getAssetId());

        Asset asset = assetRepository.findByIdWithPortfolio(requestDTO.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + requestDTO.getAssetId()));

        // Validate transaction type is allowed for this asset type
        validateTransactionForAsset(asset, requestDTO);

        Transaction transaction = transactionMapper.toEntity(requestDTO);
        transaction.setAsset(asset);

        // Update asset quantity based on transaction type
        updateAssetQuantity(asset, transaction);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Recalculate portfolio value
        Portfolio portfolio = asset.getPortfolio();
        portfolio.recalculateTotalValue();
        portfolioRepository.save(portfolio);

        assetRepository.save(asset);

        log.info("Successfully created {} transaction with ID: {} for {} asset",
                transaction.getTransactionType(), savedTransaction.getId(), asset.getType());
        return transactionMapper.toResponseDTO(savedTransaction);
    }

    /**
     * Validate that the transaction type is allowed for this asset type.
     * Demonstrates polymorphism - each asset type defines its own allowed transactions.
     */
    private void validateTransactionForAsset(Asset asset, TransactionRequestDTO requestDTO) {
        if (!asset.getAllowedTransactionTypes().contains(requestDTO.getTransactionType())) {
            throw new BadRequestException(
                    String.format("Transaction type %s is not allowed for %s assets. Allowed types: %s",
                            requestDTO.getTransactionType(),
                            asset.getType(),
                            asset.getAllowedTransactionTypes()));
        }

        // Validate quantity is valid for this asset type
        if (!asset.isQuantityValid(requestDTO.getQuantity())) {
            throw new BadRequestException(
                    String.format("Invalid quantity %s for %s asset. Minimum increment: %s",
                            requestDTO.getQuantity(),
                            asset.getType(),
                            asset.getMinimumQuantityIncrement()));
        }
    }

    /**
     * Update asset quantity based on transaction type.
     */
    private void updateAssetQuantity(Asset asset, Transaction transaction) {
        TransactionType type = transaction.getTransactionType();
        BigDecimal quantity = transaction.getQuantity();

        switch (type) {
            case BUY, TRANSFER_IN -> asset.addQuantity(quantity);
            case SELL, TRANSFER_OUT -> {
                if (asset.getQuantity().compareTo(quantity) < 0) {
                    throw new BadRequestException(
                            "Insufficient quantity. Available: " + asset.getQuantity() +
                                    ", Requested: " + quantity);
                }
                asset.subtractQuantity(quantity);
            }
            case DIVIDEND, INTEREST -> {
                // No quantity change for dividend/interest
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransactionById(Long id) {
        log.debug("Fetching transaction with ID: {}", id);
        Transaction transaction = transactionRepository.findByIdWithAsset(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return transactionMapper.toResponseDTO(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<TransactionResponseDTO> getAllTransactions(Pageable pageable) {
        log.debug("Fetching all transactions with pagination");
        Page<Transaction> page = transactionRepository.findAll(pageable);
        return PagedResponseDTO.from(page, transactionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByAssetId(Long assetId) {
        log.debug("Fetching transactions for asset ID: {}", assetId);
        return transactionRepository.findByAssetId(assetId).stream()
                .map(transactionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<TransactionResponseDTO> getTransactionsByAssetId(Long assetId, Pageable pageable) {
        log.debug("Fetching transactions for asset ID: {} with pagination", assetId);
        Page<Transaction> page = transactionRepository.findByAssetId(assetId, pageable);
        return PagedResponseDTO.from(page, transactionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByPortfolioId(Long portfolioId) {
        log.debug("Fetching transactions for portfolio ID: {}", portfolioId);
        return transactionRepository.findByAssetId(portfolioId).stream()
                .map(transactionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<TransactionResponseDTO> getTransactionsByPortfolioId(Long portfolioId, Pageable pageable) {
        log.debug("Fetching transactions for portfolio ID: {} with pagination", portfolioId);
        Page<Transaction> page = transactionRepository.findByAssetId(portfolioId, pageable);
        return PagedResponseDTO.from(page, transactionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByClientId(Long clientId) {
        log.debug("Fetching transactions for client ID: {}", clientId);
        return transactionRepository.findByAssetId(clientId).stream()
                .map(transactionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<TransactionResponseDTO> getTransactionsByClientId(Long clientId, Pageable pageable) {
        log.debug("Fetching transactions for client ID: {} with pagination", clientId);
        Page<Transaction> page = transactionRepository.findByAssetId(clientId, pageable);
        return PagedResponseDTO.from(page, transactionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<TransactionResponseDTO> getTransactionsByType(TransactionType type, Pageable pageable) {
        log.debug("Fetching transactions of type: {}", type);
        Page<Transaction> page = transactionRepository.findByTransactionType(type, pageable);
        return PagedResponseDTO.from(page, transactionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<TransactionResponseDTO> getTransactionsByDateRange(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Fetching transactions between {} and {}", startDate, endDate);
        Page<Transaction> page = transactionRepository.findByTransactionDateBetween(startDate, endDate, pageable);
        return PagedResponseDTO.from(page, transactionMapper::toResponseDTO);
    }

    @Override
    public TransactionResponseDTO updateTransaction(Long id, TransactionRequestDTO requestDTO) {
        log.info("Updating transaction with ID: {}", id);

        Transaction transaction = transactionRepository.findByIdWithAsset(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));

        Asset asset = transaction.getAsset();

        // Reverse the old transaction's effect on quantity
        reverseTransactionQuantityChange(asset, transaction);

        // Update transaction
        transactionMapper.updateEntityFromDTO(requestDTO, transaction);

        // Apply new transaction's effect on quantity
        updateAssetQuantity(asset, transaction);

        Transaction updatedTransaction = transactionRepository.save(transaction);
        assetRepository.save(asset);

        Portfolio portfolio = asset.getPortfolio();
        portfolio.recalculateTotalValue();
        portfolioRepository.save(portfolio);

        log.info("Successfully updated transaction with ID: {}", id);
        return transactionMapper.toResponseDTO(updatedTransaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        log.info("Deleting transaction with ID: {}", id);

        Transaction transaction = transactionRepository.findByIdWithAsset(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));

        Asset asset = transaction.getAsset();

        // Reverse the transaction's effect on quantity
        reverseTransactionQuantityChange(asset, transaction);

        transactionRepository.delete(transaction);
        assetRepository.save(asset);

        Portfolio portfolio = asset.getPortfolio();
        portfolio.recalculateTotalValue();
        portfolioRepository.save(portfolio);

        log.info("Successfully deleted transaction with ID: {}", id);
    }

    /**
     * Reverse a transaction's effect on asset quantity (for update/delete).
     */
    private void reverseTransactionQuantityChange(Asset asset, Transaction transaction) {
        TransactionType type = transaction.getTransactionType();
        BigDecimal quantity = transaction.getQuantity();

        switch (type) {
            case BUY, TRANSFER_IN -> asset.subtractQuantity(quantity);
            case SELL, TRANSFER_OUT -> asset.addQuantity(quantity);
            case DIVIDEND, INTEREST -> {
                // No quantity change
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionType> getTransactionTypes() {
        return Arrays.asList(TransactionType.values());
    }
}
