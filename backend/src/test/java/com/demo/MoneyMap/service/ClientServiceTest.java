package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.ClientRequestDTO;
import com.demo.MoneyMap.dto.response.ClientResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.Client;
import com.demo.MoneyMap.exception.DuplicateResourceException;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.mapper.ClientMapper;
import com.demo.MoneyMap.repository.ClientRepository;
import com.demo.MoneyMap.service.impl.ClientServiceImpl;
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
 * Test suite for ClientService focusing on client management operations.
 * 
 * This test validates the core client functionality including creation,
 * retrieval, updates, and account management. Client management is
 * fundamental for user onboarding and account administration.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Client Service - Client Management Tests")
@ActiveProfiles("test")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    
    @Mock
    private ClientMapper clientMapper;
    
    @InjectMocks
    private ClientServiceImpl clientService;
    
    // Test fixtures
    private Client testClient;
    private ClientRequestDTO validClientRequest;
    private ClientResponseDTO expectedClientResponse;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {
        // Create test client
        testClient = Client.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .address("123 Main St, City, State")
                .walletBalance(new BigDecimal("10000.00"))
                .active(true)
                .build();

        // Create valid client request
        validClientRequest = ClientRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .address("123 Main St, City, State")
                .build();

        // Create expected client response
        expectedClientResponse = ClientResponseDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .address("123 Main St, City, State")
                .active(true)
                .build();

        defaultPageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should create client successfully")
    void shouldCreateClientSuccessfully() {
        // Given: Valid client request and email doesn't exist
        given(clientRepository.existsByEmail("john.doe@example.com")).willReturn(false);
        given(clientMapper.toEntity(validClientRequest)).willReturn(testClient);
        given(clientRepository.save(testClient)).willReturn(testClient);
        given(clientMapper.toResponseDTO(testClient)).willReturn(expectedClientResponse);

        // When: Creating new client
        ClientResponseDTO result = clientService.createClient(validClientRequest);

        // Then: Should create client successfully
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getActive()).isTrue();
        
        verify(clientRepository).existsByEmail("john.doe@example.com");
        verify(clientRepository).save(testClient);
    }

    @Test
    @DisplayName("Should reject client creation with duplicate email")
    void shouldRejectClientCreationWithDuplicateEmail() {
        // Given: Email already exists
        given(clientRepository.existsByEmail("john.doe@example.com")).willReturn(true);

        // When & Then: Should throw DuplicateResourceException
        assertThatThrownBy(() -> clientService.createClient(validClientRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Client with email john.doe@example.com already exists");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve client by ID")
    void shouldRetrieveClientById() {
        // Given: Client exists
        given(clientRepository.findById(1L)).willReturn(Optional.of(testClient));
        given(clientMapper.toResponseDTO(testClient)).willReturn(expectedClientResponse);

        // When: Retrieving client
        ClientResponseDTO result = clientService.getClientById(1L);

        // Then: Should return client details
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        
        verify(clientRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle client not found gracefully")
    void shouldHandleClientNotFoundGracefully() {
        // Given: Client doesn't exist
        given(clientRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> clientService.getClientById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found with ID: 999");
    }

    @Test
    @DisplayName("Should retrieve client with portfolios")
    void shouldRetrieveClientWithPortfolios() {
        // Given: Client exists with portfolios
        given(clientRepository.findByIdWithPortfolios(1L)).willReturn(Optional.of(testClient));
        given(clientMapper.toResponseDTOWithPortfolios(testClient)).willReturn(expectedClientResponse);

        // When: Retrieving client with portfolios
        ClientResponseDTO result = clientService.getClientByIdWithPortfolios(1L);

        // Then: Should return client with portfolio details
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        
        verify(clientRepository).findByIdWithPortfolios(1L);
    }

    @Test
    @DisplayName("Should return paginated list of all clients")
    void shouldReturnPaginatedListOfAllClients() {
        // Given: Multiple clients exist
        List<Client> clients = Arrays.asList(testClient);
        Page<Client> clientPage = new PageImpl<>(clients, defaultPageable, 1);
        
        given(clientRepository.findAll(defaultPageable)).willReturn(clientPage);
        given(clientMapper.toResponseDTO(testClient)).willReturn(expectedClientResponse);

        // When: Retrieving all clients
        PagedResponseDTO<ClientResponseDTO> result = clientService.getAllClients(defaultPageable);

        // Then: Should return paginated response
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("john.doe@example.com");
        
        verify(clientRepository).findAll(defaultPageable);
    }

    @Test
    @DisplayName("Should return list of active clients")
    void shouldReturnListOfActiveClients() {
        // Given: Active clients exist
        List<Client> activeClients = Arrays.asList(testClient);
        given(clientRepository.findByActiveTrue()).willReturn(activeClients);
        given(clientMapper.toResponseDTO(testClient)).willReturn(expectedClientResponse);

        // When: Retrieving active clients
        List<ClientResponseDTO> result = clientService.getActiveClients();

        // Then: Should return only active clients
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActive()).isTrue();
        
        verify(clientRepository).findByActiveTrue();
    }

    @Test
    @DisplayName("Should search clients by term")
    void shouldSearchClientsByTerm() {
        // Given: Clients match search term
        List<Client> matchingClients = Arrays.asList(testClient);
        Page<Client> clientPage = new PageImpl<>(matchingClients, defaultPageable, 1);
        
        given(clientRepository.searchClients("John", defaultPageable)).willReturn(clientPage);
        given(clientMapper.toResponseDTO(testClient)).willReturn(expectedClientResponse);

        // When: Searching clients
        PagedResponseDTO<ClientResponseDTO> result = clientService.searchClients("John", defaultPageable);

        // Then: Should return matching clients
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
        
        verify(clientRepository).searchClients("John", defaultPageable);
    }

    @Test
    @DisplayName("Should update client successfully")
    void shouldUpdateClientSuccessfully() {
        // Given: Existing client with valid update request
        ClientRequestDTO updateRequest = ClientRequestDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("+0987654321")
                .address("456 Oak Ave, City, State")
                .build();

        given(clientRepository.findById(1L)).willReturn(Optional.of(testClient));
        given(clientRepository.existsByEmailAndIdNot("jane.smith@example.com", 1L)).willReturn(false);
        given(clientRepository.save(testClient)).willReturn(testClient);
        given(clientMapper.toResponseDTO(testClient)).willReturn(expectedClientResponse);

        // When: Updating client
        ClientResponseDTO result = clientService.updateClient(1L, updateRequest);

        // Then: Should update client successfully
        assertThat(result).isNotNull();
        verify(clientRepository).save(testClient);
        verify(clientMapper).updateEntityFromDTO(updateRequest, testClient);
    }

    @Test
    @DisplayName("Should reject client update with duplicate email")
    void shouldRejectClientUpdateWithDuplicateEmail() {
        // Given: Another client has the same email
        ClientRequestDTO updateRequest = ClientRequestDTO.builder()
                .email("existing.email@example.com")
                .build();

        given(clientRepository.findById(1L)).willReturn(Optional.of(testClient));
        given(clientRepository.existsByEmailAndIdNot("existing.email@example.com", 1L)).willReturn(true);

        // When & Then: Should throw DuplicateResourceException
        assertThatThrownBy(() -> clientService.updateClient(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Client with email existing.email@example.com already exists");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should deactivate client successfully")
    void shouldDeactivateClientSuccessfully() {
        // Given: Active client exists
        given(clientRepository.findById(1L)).willReturn(Optional.of(testClient));
        given(clientRepository.save(testClient)).willReturn(testClient);

        // When: Deactivating client
        clientService.deactivateClient(1L);

        // Then: Client should be deactivated
        assertThat(testClient.getActive()).isFalse();
        verify(clientRepository).save(testClient);
    }

    @Test
    @DisplayName("Should activate client successfully")
    void shouldActivateClientSuccessfully() {
        // Given: Inactive client exists
        testClient.setActive(false);
        given(clientRepository.findById(1L)).willReturn(Optional.of(testClient));
        given(clientRepository.save(testClient)).willReturn(testClient);

        // When: Activating client
        clientService.activateClient(1L);

        // Then: Client should be activated
        assertThat(testClient.getActive()).isTrue();
        verify(clientRepository).save(testClient);
    }

    @Test
    @DisplayName("Should delete client successfully")
    void shouldDeleteClientSuccessfully() {
        // Given: Client exists
        given(clientRepository.findById(1L)).willReturn(Optional.of(testClient));

        // When: Deleting client
        clientService.deleteClient(1L);

        // Then: Should trigger deletion
        verify(clientRepository).delete(testClient);
    }

    @Test
    @DisplayName("Should return active client count")
    void shouldReturnActiveClientCount() {
        // Given: Specific number of active clients
        given(clientRepository.countByActiveTrue()).willReturn(25L);

        // When: Getting active client count
        long result = clientService.getActiveClientCount();

        // Then: Should return correct count
        assertThat(result).isEqualTo(25L);
        verify(clientRepository).countByActiveTrue();
    }

    @Test
    @DisplayName("Should handle update for non-existent client")
    void shouldHandleUpdateForNonExistentClient() {
        // Given: Client doesn't exist
        given(clientRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> clientService.updateClient(999L, validClientRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found with ID: 999");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle deactivation for non-existent client")
    void shouldHandleDeactivationForNonExistentClient() {
        // Given: Client doesn't exist
        given(clientRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> clientService.deactivateClient(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found with ID: 999");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle activation for non-existent client")
    void shouldHandleActivationForNonExistentClient() {
        // Given: Client doesn't exist
        given(clientRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> clientService.activateClient(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found with ID: 999");

        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle deletion for non-existent client")
    void shouldHandleDeletionForNonExistentClient() {
        // Given: Client doesn't exist
        given(clientRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then: Should throw ResourceNotFoundException
        assertThatThrownBy(() -> clientService.deleteClient(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found with ID: 999");

        verify(clientRepository, never()).delete(any());
    }
}
