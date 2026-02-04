package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.PortfolioRequestDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.dto.response.PortfolioResponseDTO;
import com.demo.MoneyMap.entity.Client;
import com.demo.MoneyMap.entity.Portfolio;
import com.demo.MoneyMap.exception.DuplicateResourceException;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.mapper.PortfolioMapper;
import com.demo.MoneyMap.repository.AssetRepository;
import com.demo.MoneyMap.repository.ClientRepository;
import com.demo.MoneyMap.repository.PortfolioRepository;
import com.demo.MoneyMap.service.impl.PortfolioServiceImpl;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for PortfolioService.
 * 
 * This test class validates the core business logic of portfolio management,
 * with special focus on the critical one-to-one relationship between clients
 * and portfolios, which is a key business rule in this system.
 * 
 * Test Coverage:
 * - Happy path scenarios (normal operations)
 * - Edge cases and boundary conditions
 * - Error handling and validation
 * - Business rule enforcement
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Portfolio Service - Business Logic Tests")
@ActiveProfiles("test")
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;
    
    @Mock
    private ClientRepository clientRepository;
    
    @Mock
    private AssetRepository assetRepository;
    
    @Mock
    private PortfolioMapper portfolioMapper;
    
    @InjectMocks
    private PortfolioServiceImpl portfolioService;
    
    // Test data fixtures
    private Client testClient;
    private Portfolio testPortfolio;
    private PortfolioRequestDTO validPortfolioRequest;
    private PortfolioResponseDTO expectedResponse;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {
        // Create realistic test data that mirrors real-world scenarios
        testClient = Client.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        testPortfolio = Portfolio.builder()
                .id(1L)
                .name("Growth Portfolio")
                .description("Long-term growth focused portfolio")
                .totalValue(new BigDecimal("50000.00"))
                .active(true)
                .client(testClient)
                .build();

        validPortfolioRequest = PortfolioRequestDTO.builder()
                .name("Growth Portfolio")
                .description("Long-term growth focused portfolio")
                .clientId(1L)
                .build();

        expectedResponse = PortfolioResponseDTO.builder()
                .id(1L)
                .name("Growth Portfolio")
                .description("Long-term growth focused portfolio")
                .totalValue(new BigDecimal("50000.00"))
                .active(true)
                .clientId(1L)
                .clientName("John Doe")
                .build();

        defaultPageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should create portfolio successfully for new client")
    void shouldCreatePortfolioSuccessfullyForNewClient() {
        // Given: A valid client without an existing portfolio
        given(clientRepository.findById(1L)).willReturn(Optional.of(testClient));
        given(portfolioRepository.existsByClientId(1L)).willReturn(false);
        given(portfolioMapper.toEntity(validPortfolioRequest)).willReturn(testPortfolio);
        given(portfolioRepository.save(testPortfolio)).willReturn(testPortfolio);
        given(portfolioMapper.toResponseDTO(testPortfolio)).willReturn(expectedResponse);

        // When: Creating a new portfolio
        PortfolioResponseDTO result = portfolioService.createPortfolio(validPortfolioRequest);

        // Then: Portfolio should be created successfully
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Growth Portfolio");
        assertThat(result.getClientId()).isEqualTo(1L);
        assertThat(result.getClientName()).isEqualTo("John Doe");
        
        // Verify database interactions
        verify(portfolioRepository).save(testPortfolio);
        verify(clientRepository).findById(1L);
        verify(portfolioRepository).existsByClientId(1L);
    }

    @Test
    @DisplayName("Should reject portfolio creation for non-existent client")
    void shouldRejectPortfolioCreationForNonExistentClient() {
        // Given: No client exists with the given ID
        given(clientRepository.findById(999L)).willReturn(Optional.empty());

        PortfolioRequestDTO invalidRequest = PortfolioRequestDTO.builder()
                .name("Test Portfolio")
                .clientId(999L)
                .build();

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> portfolioService.createPortfolio(invalidRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found with ID: 999");

        // Verify no database save operation occurs
        verify(portfolioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject duplicate portfolio for same client - Business Rule Enforcement")
    void shouldRejectDuplicatePortfolioForSameClient() {
        // Given: Client already has a portfolio (violates one-to-one rule)
        given(clientRepository.findById(1L)).willReturn(Optional.of(testClient));
        given(portfolioRepository.existsByClientId(1L)).willReturn(true);

        // When & Then: Should throw DuplicateResourceException
        assertThatThrownBy(() -> portfolioService.createPortfolio(validPortfolioRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Client already has a portfolio. Each client can only have one portfolio.");

        // Verify no database save operation occurs
        verify(portfolioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve portfolio with full details")
    void shouldRetrievePortfolioWithFullDetails() {
        // Given: Portfolio exists in database
        given(portfolioRepository.findById(1L)).willReturn(Optional.of(testPortfolio));
        given(portfolioMapper.toResponseDTO(testPortfolio)).willReturn(expectedResponse);

        // When: Retrieving portfolio by ID
        PortfolioResponseDTO result = portfolioService.getPortfolioById(1L);

        // Then: Should return complete portfolio information
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Growth Portfolio");
        assertThat(result.getTotalValue()).isEqualTo(new BigDecimal("50000.00"));
        
        verify(portfolioRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle portfolio not found gracefully")
    void shouldHandlePortfolioNotFoundGracefully() {
        // Given: Portfolio doesn't exist
        given(portfolioRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> portfolioService.getPortfolioById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Portfolio not found with ID: 999");
    }

    @Test
    @DisplayName("Should retrieve portfolio with assets for valuation")
    void shouldRetrievePortfolioWithAssetsForValuation() {
        // Given: Portfolio with assets exists
        given(portfolioRepository.findByIdWithAssets(1L)).willReturn(Optional.of(testPortfolio));
        given(portfolioMapper.toResponseDTOWithAssets(testPortfolio)).willReturn(expectedResponse);

        // When: Retrieving portfolio with assets
        PortfolioResponseDTO result = portfolioService.getPortfolioByIdWithAssets(1L);

        // Then: Should return portfolio with asset details
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        
        verify(portfolioRepository).findByIdWithAssets(1L);
    }

    @Test
    @DisplayName("Should return paginated list of portfolios")
    void shouldReturnPaginatedListOfPortfolios() {
        // Given: Multiple portfolios exist
        List<Portfolio> portfolios = Arrays.asList(testPortfolio);
        Page<Portfolio> portfolioPage = new PageImpl<>(portfolios, defaultPageable, 1);
        
        given(portfolioRepository.findAll(defaultPageable)).willReturn(portfolioPage);
        given(portfolioMapper.toResponseDTO(testPortfolio)).willReturn(expectedResponse);

        // When: Retrieving all portfolios with pagination
        PagedResponseDTO<PortfolioResponseDTO> result = portfolioService.getAllPortfolios(defaultPageable);

        // Then: Should return paginated response
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Growth Portfolio");
        
        verify(portfolioRepository).findAll(defaultPageable);
    }

    @Test
    @DisplayName("Should calculate total portfolio value correctly")
    void shouldCalculateTotalPortfolioValueCorrectly() {
        // Given: Client has portfolio with specific value
        BigDecimal expectedTotalValue = new BigDecimal("75000.00");
        given(portfolioRepository.getTotalValueByClientId(1L)).willReturn(expectedTotalValue);

        // When: Calculating total value for client
        BigDecimal result = portfolioService.getTotalValueByClientId(1L);

        // Then: Should return correct total value
        assertThat(result).isEqualTo(expectedTotalValue);
        verify(portfolioRepository).getTotalValueByClientId(1L);
    }

    @Test
    @DisplayName("Should recalculate portfolio value based on current asset prices")
    void shouldRecalculatePortfolioValueBasedOnCurrentAssetPrices() {
        // Given: Portfolio exists with assets
        given(portfolioRepository.findByIdWithAssets(1L)).willReturn(Optional.of(testPortfolio));
        given(portfolioRepository.save(testPortfolio)).willReturn(testPortfolio);
        given(portfolioMapper.toResponseDTO(testPortfolio)).willReturn(expectedResponse);

        // When: Recalculating portfolio value
        PortfolioResponseDTO result = portfolioService.recalculateTotalValue(1L);

        // Then: Should trigger recalculation and save updated value
        assertThat(result).isNotNull();
        // Note: recalculateTotalValue() is called on the real entity, not a mock
        verify(portfolioRepository).save(testPortfolio);
        verify(portfolioMapper).toResponseDTO(testPortfolio);
    }

    @Test
    @DisplayName("Should deactivate portfolio for compliance")
    void shouldDeactivatePortfolioForCompliance() {
        // Given: Active portfolio exists
        given(portfolioRepository.findById(1L)).willReturn(Optional.of(testPortfolio));
        given(portfolioRepository.save(testPortfolio)).willReturn(testPortfolio);

        // When: Deactivating portfolio
        portfolioService.deactivatePortfolio(1L);

        // Then: Portfolio should be marked as inactive
        assertThat(testPortfolio.getActive()).isFalse();
        verify(portfolioRepository).save(testPortfolio);
    }

    @Test
    @DisplayName("Should reactivate previously deactivated portfolio")
    void shouldReactivatePreviouslyDeactivatedPortfolio() {
        // Given: Inactive portfolio exists
        testPortfolio.setActive(false);
        given(portfolioRepository.findById(1L)).willReturn(Optional.of(testPortfolio));
        given(portfolioRepository.save(testPortfolio)).willReturn(testPortfolio);

        // When: Activating portfolio
        portfolioService.activatePortfolio(1L);

        // Then: Portfolio should be marked as active
        assertThat(testPortfolio.getActive()).isTrue();
        verify(portfolioRepository).save(testPortfolio);
    }

    @Test
    @DisplayName("Should handle portfolio deletion with proper cleanup")
    void shouldHandlePortfolioDeletionWithProperCleanup() {
        // Given: Portfolio exists
        given(portfolioRepository.findById(1L)).willReturn(Optional.of(testPortfolio));

        // When: Deleting portfolio
        portfolioService.deletePortfolio(1L);

        // Then: Should trigger proper deletion
        verify(portfolioRepository).delete(testPortfolio);
    }

    @Test
    @DisplayName("Should update portfolio while maintaining business rules")
    void shouldUpdatePortfolioWhileMaintainingBusinessRules() {
        // Given: Existing portfolio with valid update request
        PortfolioRequestDTO updateRequest = PortfolioRequestDTO.builder()
                .name("Updated Growth Portfolio")
                .description("Updated description")
                .clientId(1L) // Same client
                .build();

        given(portfolioRepository.findById(1L)).willReturn(Optional.of(testPortfolio));
        given(portfolioRepository.save(testPortfolio)).willReturn(testPortfolio);
        given(portfolioMapper.toResponseDTO(testPortfolio)).willReturn(expectedResponse);

        // When: Updating portfolio
        PortfolioResponseDTO result = portfolioService.updatePortfolio(1L, updateRequest);

        // Then: Should update and save portfolio
        assertThat(result).isNotNull();
        verify(portfolioRepository).save(testPortfolio);
        verify(portfolioMapper).updateEntityFromDTO(updateRequest, testPortfolio);
    }
}
