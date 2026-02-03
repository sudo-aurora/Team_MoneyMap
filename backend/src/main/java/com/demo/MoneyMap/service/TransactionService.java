package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.TransactionRequestDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.dto.response.TransactionResponseDTO;
import com.demo.MoneyMap.entity.enums.TransactionType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for transaction management operations.
 * Follows Interface Segregation Principle (ISP) from SOLID.
 */
public interface TransactionService {

    /**
     * Create a new transaction.
     *
     * @param requestDTO the transaction data to create
     * @return the created transaction
     */
    TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO);

    /**
     * Get a transaction by ID.
     *
     * @param id the transaction ID
     * @return the transaction details
     */
    TransactionResponseDTO getTransactionById(Long id);

    /**
     * Get all transactions with pagination.
     *
     * @param pageable pagination parameters
     * @return paginated list of transactions
     */
    PagedResponseDTO<TransactionResponseDTO> getAllTransactions(Pageable pageable);

    /**
     * Get all transactions for a specific asset.
     *
     * @param assetId the asset ID
     * @return list of transactions
     */
    List<TransactionResponseDTO> getTransactionsByAssetId(Long assetId);

    /**
     * Get all transactions for an asset with pagination.
     *
     * @param assetId the asset ID
     * @param pageable pagination parameters
     * @return paginated list of transactions
     */
    PagedResponseDTO<TransactionResponseDTO> getTransactionsByAssetId(Long assetId, Pageable pageable);

    /**
     * Get all transactions for a portfolio.
     *
     * @param portfolioId the portfolio ID
     * @return list of transactions
     */
    List<TransactionResponseDTO> getTransactionsByPortfolioId(Long portfolioId);

    /**
     * Get all transactions for a portfolio with pagination.
     *
     * @param portfolioId the portfolio ID
     * @param pageable pagination parameters
     * @return paginated list of transactions
     */
    PagedResponseDTO<TransactionResponseDTO> getTransactionsByPortfolioId(Long portfolioId, Pageable pageable);

    /**
     * Get all transactions for a client.
     *
     * @param clientId the client ID
     * @return list of transactions
     */
    List<TransactionResponseDTO> getTransactionsByClientId(Long clientId);

    /**
     * Get all transactions for a client with pagination.
     *
     * @param clientId the client ID
     * @param pageable pagination parameters
     * @return paginated list of transactions
     */
    PagedResponseDTO<TransactionResponseDTO> getTransactionsByClientId(Long clientId, Pageable pageable);

    /**
     * Get transactions by type.
     *
     * @param transactionType the transaction type
     * @param pageable pagination parameters
     * @return paginated list of transactions
     */
    PagedResponseDTO<TransactionResponseDTO> getTransactionsByType(TransactionType transactionType, Pageable pageable);

    /**
     * Get transactions within a date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination parameters
     * @return paginated list of transactions
     */
    PagedResponseDTO<TransactionResponseDTO> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Update an existing transaction.
     *
     * @param id the transaction ID
     * @param requestDTO the updated transaction data
     * @return the updated transaction
     */
    TransactionResponseDTO updateTransaction(Long id, TransactionRequestDTO requestDTO);

    /**
     * Delete a transaction permanently.
     *
     * @param id the transaction ID
     */
    void deleteTransaction(Long id);

    /**
     * Get all transaction types.
     *
     * @return list of transaction types
     */
    List<TransactionType> getTransactionTypes();
}
