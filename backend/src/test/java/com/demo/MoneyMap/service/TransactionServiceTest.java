package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.TransactionRequestDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.dto.response.TransactionResponseDTO;
import com.demo.MoneyMap.entity.Portfolio;
import com.demo.MoneyMap.entity.StockAsset;
import com.demo.MoneyMap.entity.Transaction;
import com.demo.MoneyMap.entity.enums.TransactionType;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.mapper.TransactionMapper;
import com.demo.MoneyMap.repository.AssetRepository;
import com.demo.MoneyMap.repository.PortfolioRepository;
import com.demo.MoneyMap.repository.TransactionRepository;
import com.demo.MoneyMap.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Test suite for TransactionService focusing on transaction management.
 * 
 * This test validates the core transaction functionality including creation,
 * retrieval, updates, and deletion with proper asset quantity management.
 * Transaction tracking is critical for audit trails and portfolio management.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction Service - Transaction Management Tests")
@ActiveProfiles("test")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private AssetRepository assetRepository;
    
    @Mock
    private PortfolioRepository portfolioRepository;
    
    @Mock
    private TransactionMapper transactionMapper;
    
    @InjectMocks
    private TransactionServiceImpl transactionService;
    
    // Test fixtures
    private Portfolio testPortfolio;
    private StockAsset testAsset;
    private Transaction testTransaction;
    private TransactionRequestDTO validTransactionRequest;
    private TransactionResponseDTO expectedTransactionResponse;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {
        // Create test portfolio
        testPortfolio = Portfolio.builder()
                .id(1L)
                .name("Growth Portfolio")
                .totalValue(new BigDecimal("50000.00"))
                .build();

        // Create test asset
        testAsset = StockAsset.builder()
                .id(1L)
                .symbol("AAPL")
                .name("Apple Inc.")
                .quantity(new BigDecimal("100"))
                .purchasePrice(new BigDecimal("150.00"))
                .currentPrice(new BigDecimal("175.50"))
                .portfolio(testPortfolio)
                .build();

        // Create test transaction
        testTransaction = Transaction.builder()
                .id(1L)
                .asset(testAsset)
                .transactionType(TransactionType.BUY)
                .quantity(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .totalAmount(new BigDecimal("1500.00"))
                .transactionDate(LocalDateTime.now())
                .notes("Purchased 10 shares of AAPL")
                .build();

        // Create valid transaction request
        validTransactionRequest = TransactionRequestDTO.builder()
                .assetId(1L)
                .transactionType(TransactionType.BUY)
                .quantity(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .transactionDate(LocalDateTime.now())
                .notes("Purchased 10 shares of AAPL")
                .build();

        // Create expected transaction response
        expectedTransactionResponse = TransactionResponseDTO.builder()
                .id(1L)
                .assetId(1L)
                .transactionType(TransactionType.BUY)
                .quantity(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .totalAmount(new BigDecimal("1500.00"))
                .transactionDate(LocalDateTime.now())
                .notes("Purchased 10 shares of AAPL")
                .build();

        defaultPageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should create BUY transaction successfully")
    void shouldCreateBuyTransactionSuccessfully() {
        // Given: Valid transaction request and asset exists
        given(assetRepository.findByIdWithPortfolio(1L)).willReturn(Optional.of(testAsset));
        given(transactionMapper.toEntity(validTransactionRequest)).willReturn(testTransaction);
        given(transactionRepository.save(testTransaction)).willReturn(testTransaction);
        given(transactionMapper.toResponseDTO(testTransaction)).willReturn(expectedTransactionResponse);

        // When: Creating transaction
        TransactionResponseDTO result = transactionService.createTransaction(validTransactionRequest);

        // Then: Should create transaction successfully
        assertThat(result).isNotNull();
        assertThat(result.getTransactionType()).isEqualTo(TransactionType.BUY);
        assertThat(result.getQuantity()).isEqualTo(new BigDecimal("10"));
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("1500.00"));
        
        verify(transactionRepository).save(testTransaction);
        verify(portfolioRepository).save(testPortfolio);
        verify(assetRepository).save(testAsset);
    }

    @Test
    @DisplayName("Should reject transaction for non-existent asset")
    void shouldRejectTransactionForNonExistentAsset() {
        // Given: Asset doesn't exist
        given(assetRepository.findByIdWithPortfolio(999L)).willReturn(Optional.empty());

        TransactionRequestDTO invalidRequest = TransactionRequestDTO.builder()
                .assetId(999L)
                .transactionType(TransactionType.BUY)
                .build();

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> transactionService.createTransaction(invalidRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Asset not found with ID: 999");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve transaction by ID")
    void shouldRetrieveTransactionById() {
        // Given: Transaction exists
        given(transactionRepository.findByIdWithAsset(1L)).willReturn(Optional.of(testTransaction));
        given(transactionMapper.toResponseDTO(testTransaction)).willReturn(expectedTransactionResponse);

        // When: Retrieving transaction
        TransactionResponseDTO result = transactionService.getTransactionById(1L);

        // Then: Should return transaction details
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTransactionType()).isEqualTo(TransactionType.BUY);
        
        verify(transactionRepository).findByIdWithAsset(1L);
    }

    @Test
    @DisplayName("Should handle transaction not found gracefully")
    void shouldHandleTransactionNotFoundGracefully() {
        // Given: Transaction doesn't exist
        given(transactionRepository.findByIdWithAsset(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> transactionService.getTransactionById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found with ID: 999");
    }

    @Test
    @DisplayName("Should return paginated list of all transactions")
    void shouldReturnPaginatedListOfAllTransactions() {
        // Given: Multiple transactions exist
        List<Transaction> transactions = Arrays.asList(testTransaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, defaultPageable, 1);
        
        given(transactionRepository.findAll(defaultPageable)).willReturn(transactionPage);
        given(transactionMapper.toResponseDTO(testTransaction)).willReturn(expectedTransactionResponse);

        // When: Retrieving all transactions
        PagedResponseDTO<TransactionResponseDTO> result = transactionService.getAllTransactions(defaultPageable);

        // Then: Should return paginated response
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        
        verify(transactionRepository).findAll(defaultPageable);
    }

    @Test
    @DisplayName("Should retrieve transactions by asset ID")
    void shouldRetrieveTransactionsByAssetId() {
        // Given: Asset has transactions
        List<Transaction> transactions = Arrays.asList(testTransaction);
        given(transactionRepository.findByAssetId(1L)).willReturn(transactions);
        given(transactionMapper.toResponseDTO(testTransaction)).willReturn(expectedTransactionResponse);

        // When: Retrieving transactions by asset
        List<TransactionResponseDTO> result = transactionService.getTransactionsByAssetId(1L);

        // Then: Should return asset transactions
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAssetId()).isEqualTo(1L);
        
        verify(transactionRepository).findByAssetId(1L);
    }

    @Test
    @DisplayName("Should retrieve transactions by asset ID with pagination")
    void shouldRetrieveTransactionsByAssetIdWithPagination() {
        // Given: Asset has multiple transactions
        List<Transaction> transactions = Arrays.asList(testTransaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, defaultPageable, 1);
        
        given(transactionRepository.findByAssetId(1L, defaultPageable)).willReturn(transactionPage);
        given(transactionMapper.toResponseDTO(testTransaction)).willReturn(expectedTransactionResponse);

        // When: Retrieving transactions with pagination
        PagedResponseDTO<TransactionResponseDTO> result = transactionService.getTransactionsByAssetId(1L, defaultPageable);

        // Then: Should return paginated response
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(transactionRepository).findByAssetId(1L, defaultPageable);
    }

    @Test
    @DisplayName("Should retrieve transactions by portfolio ID")
    void shouldRetrieveTransactionsByPortfolioId() {
        // Given: Portfolio has transactions
        List<Transaction> transactions = Arrays.asList(testTransaction);
        given(transactionRepository.findByAssetId(1L)).willReturn(transactions);
        given(transactionMapper.toResponseDTO(testTransaction)).willReturn(expectedTransactionResponse);

        // When: Retrieving transactions by portfolio
        List<TransactionResponseDTO> result = transactionService.getTransactionsByPortfolioId(1L);

        // Then: Should return portfolio transactions
        assertThat(result).hasSize(1);
        
        verify(transactionRepository).findByAssetId(1L);
    }

    @Test
    @DisplayName("Should retrieve transactions by client ID")
    void shouldRetrieveTransactionsByClientId() {
        // Given: Client has transactions
        List<Transaction> transactions = Arrays.asList(testTransaction);
        given(transactionRepository.findByAssetId(1L)).willReturn(transactions);
        given(transactionMapper.toResponseDTO(testTransaction)).willReturn(expectedTransactionResponse);

        // When: Retrieving transactions by client
        List<TransactionResponseDTO> result = transactionService.getTransactionsByClientId(1L);

        // Then: Should return client transactions
        assertThat(result).hasSize(1);
        
        verify(transactionRepository).findByAssetId(1L);
    }

    @Test
    @DisplayName("Should retrieve transactions by type")
    void shouldRetrieveTransactionsByType() {
        // Given: Transactions of specific type exist
        List<Transaction> transactions = Arrays.asList(testTransaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, defaultPageable, 1);
        
        given(transactionRepository.findByTransactionType(TransactionType.BUY, defaultPageable))
                .willReturn(transactionPage);
        given(transactionMapper.toResponseDTO(testTransaction)).willReturn(expectedTransactionResponse);

        // When: Retrieving transactions by type
        PagedResponseDTO<TransactionResponseDTO> result = transactionService.getTransactionsByType(
                TransactionType.BUY, defaultPageable);

        // Then: Should return filtered transactions
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTransactionType()).isEqualTo(TransactionType.BUY);
        
        verify(transactionRepository).findByTransactionType(TransactionType.BUY, defaultPageable);
    }

    @Test
    @DisplayName("Should retrieve transactions by date range")
    void shouldRetrieveTransactionsByDateRange() {
        // Given: Transactions exist within date range
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<Transaction> transactions = Arrays.asList(testTransaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, defaultPageable, 1);
        
        given(transactionRepository.findByTransactionDateBetween(startDate, endDate, defaultPageable))
                .willReturn(transactionPage);
        given(transactionMapper.toResponseDTO(testTransaction)).willReturn(expectedTransactionResponse);

        // When: Retrieving transactions by date range
        PagedResponseDTO<TransactionResponseDTO> result = transactionService.getTransactionsByDateRange(
                startDate, endDate, defaultPageable);

        // Then: Should return filtered transactions
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        verify(transactionRepository).findByTransactionDateBetween(startDate, endDate, defaultPageable);
    }

    @Test
    @DisplayName("Should update existing transaction")
    void shouldUpdateExistingTransaction() {
        // Given: Existing transaction with update request
        TransactionRequestDTO updateRequest = TransactionRequestDTO.builder()
                .assetId(1L)
                .transactionType(TransactionType.BUY)
                .quantity(new BigDecimal("15"))
                .pricePerUnit(new BigDecimal("155.00"))
                .notes("Updated transaction")
                .build();

        given(transactionRepository.findByIdWithAsset(1L)).willReturn(Optional.of(testTransaction));
        given(transactionRepository.save(testTransaction)).willReturn(testTransaction);
        given(transactionMapper.toResponseDTO(testTransaction)).willReturn(expectedTransactionResponse);

        // When: Updating transaction
        TransactionResponseDTO result = transactionService.updateTransaction(1L, updateRequest);

        // Then: Should update and return transaction
        assertThat(result).isNotNull();
        verify(transactionRepository).save(testTransaction);
        verify(portfolioRepository).save(testPortfolio);
        verify(assetRepository).save(testAsset);
    }

    @Test
    @DisplayName("Should delete transaction with proper cleanup")
    void shouldDeleteTransactionWithProperCleanup() {
        // Given: Transaction exists
        given(transactionRepository.findByIdWithAsset(1L)).willReturn(Optional.of(testTransaction));

        // When: Deleting transaction
        transactionService.deleteTransaction(1L);

        // Then: Should trigger proper deletion and cleanup
        verify(transactionRepository).delete(testTransaction);
        verify(portfolioRepository).save(testPortfolio);
        verify(assetRepository).save(testAsset);
    }

    @Test
    @DisplayName("Should return all transaction types")
    void shouldReturnAllTransactionTypes() {
        // When: Getting transaction types
        List<TransactionType> result = transactionService.getTransactionTypes();

        // Then: Should return all available types
        assertThat(result).isNotNull();
        assertThat(result).contains(TransactionType.BUY, TransactionType.SELL, TransactionType.DIVIDEND);
    }

    @Test
    @DisplayName("Should handle update for non-existent transaction")
    void shouldHandleUpdateForNonExistentTransaction() {
        // Given: Transaction doesn't exist
        given(transactionRepository.findByIdWithAsset(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> transactionService.updateTransaction(999L, validTransactionRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found with ID: 999");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle delete transaction for non-existent transaction")
    void shouldHandleDeleteTransactionForNonExistentTransaction() {
        // Given: Transaction doesn't exist
        given(transactionRepository.findByIdWithAsset(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> transactionService.deleteTransaction(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found with ID: 999");

        verify(transactionRepository, never()).delete(any());
    }
}
