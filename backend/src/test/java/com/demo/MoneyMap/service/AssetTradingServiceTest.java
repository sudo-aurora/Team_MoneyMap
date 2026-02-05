package com.demo.MoneyMap.service;

import com.demo.MoneyMap.config.AssetFactory;
import com.demo.MoneyMap.dto.request.TransactionRequestDTO;
import com.demo.MoneyMap.dto.response.TransactionResponseDTO;
import com.demo.MoneyMap.entity.Client;
import com.demo.MoneyMap.entity.Portfolio;
import com.demo.MoneyMap.entity.StockAsset;
import com.demo.MoneyMap.entity.enums.TransactionType;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.repository.AssetRepository;
import com.demo.MoneyMap.repository.ClientRepository;
import com.demo.MoneyMap.repository.PortfolioRepository;
import com.demo.MoneyMap.service.impl.AssetTradingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Test suite for AssetTradingService focusing on trading operations.
 * 
 * This test validates the core trading functionality including buy/sell operations,
 * wallet management, and asset availability checks. Trading operations are critical
 * for the demo as they represent the core business functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Asset Trading Service - Trading Operations Tests")
@ActiveProfiles("test")
class AssetTradingServiceTest {

    @Mock
    private ClientRepository clientRepository;
    
    @Mock
    private AssetRepository assetRepository;
    
    @Mock
    private PortfolioRepository portfolioRepository;
    
    @Mock
    private TransactionService transactionService;
    
    @Mock
    private AssetFactory assetFactory;
    
    @InjectMocks
    private AssetTradingServiceImpl assetTradingService;
    
    // Test fixtures
    private Client testClient;
    private Portfolio testPortfolio;
    private StockAsset testAsset;
    private TransactionResponseDTO mockBuyResponse;
    private BigDecimal testQuantity;
    private BigDecimal testPrice;

    @BeforeEach
    void setUp() {
        // Create test client with wallet
        testClient = Client.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .walletBalance(new BigDecimal("10000.00"))
                .build();

        // Create test portfolio
        testPortfolio = Portfolio.builder()
                .id(1L)
                .name("Primary Portfolio")
                .client(testClient)
                .totalValue(new BigDecimal("5000.00"))
                .build();

        // Create test asset
        testAsset = StockAsset.builder()
                .id(1L)
                .symbol("AAPL")
                .name("Apple Inc.")
                .quantity(new BigDecimal("50"))
                .purchasePrice(new BigDecimal("150.00"))
                .currentPrice(new BigDecimal("175.50"))
                .portfolio(testPortfolio)
                .build();

        // Create mock transaction responses
        mockBuyResponse = TransactionResponseDTO.builder()
                .id(1L)
                .assetId(1L)
                .transactionType(TransactionType.BUY)
                .quantity(new BigDecimal("10"))
                .pricePerUnit(new BigDecimal("150.00"))
                .totalAmount(new BigDecimal("1500.00"))
                .transactionDate(LocalDateTime.now())
                .notes("Purchased 10 shares of AAPL at $150.00")
                .build();

        testQuantity = new BigDecimal("10");
        testPrice = new BigDecimal("150.00");
    }

    @Test
    @DisplayName("Should reject buy order for non-existent client")
    void shouldRejectBuyOrderForNonExistentClient() {
        // Given: Client doesn't exist
        given(clientRepository.findByIdWithLock(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> assetTradingService.buyAsset(999L, "AAPL", testQuantity, testPrice))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found with ID: 999");

        verify(transactionService, never()).createTransaction(any());
    }

    @Test
    @DisplayName("Should check if client has sufficient funds")
    void shouldCheckIfClientHasSufficientFunds() {
        // Given: Client exists with specific balance
        given(clientRepository.findById(1L)).willReturn(Optional.of(testClient));

        // When: Checking sufficient funds
        boolean hasFunds = assetTradingService.hasSufficientFunds(1L, new BigDecimal("5000.00"));
        boolean hasNoFunds = assetTradingService.hasSufficientFunds(1L, new BigDecimal("15000.00"));

        // Then: Should return correct results
        assertThat(hasFunds).isTrue();
        assertThat(hasNoFunds).isFalse();
        verify(clientRepository, times(2)).findById(1L);
    }
}
