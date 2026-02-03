package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.ClientRequestDTO;
import com.demo.MoneyMap.dto.response.ClientResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for client management operations.
 * Follows Interface Segregation Principle (ISP) from SOLID.
 */
public interface ClientService {

    /**
     * Create a new client.
     *
     * @param requestDTO the client data to create
     * @return the created client
     */
    ClientResponseDTO createClient(ClientRequestDTO requestDTO);

    /**
     * Get a client by ID.
     *
     * @param id the client ID
     * @return the client details
     */
    ClientResponseDTO getClientById(Long id);

    /**
     * Get a client by ID with their portfolios.
     *
     * @param id the client ID
     * @return the client details with portfolios
     */
    ClientResponseDTO getClientByIdWithPortfolios(Long id);

    /**
     * Get all clients with pagination.
     *
     * @param pageable pagination parameters
     * @return paginated list of clients
     */
    PagedResponseDTO<ClientResponseDTO> getAllClients(Pageable pageable);

    /**
     * Get all active clients.
     *
     * @return list of active clients
     */
    List<ClientResponseDTO> getActiveClients();

    /**
     * Search clients by name or email.
     *
     * @param searchTerm the search term
     * @param pageable pagination parameters
     * @return paginated list of matching clients
     */
    PagedResponseDTO<ClientResponseDTO> searchClients(String searchTerm, Pageable pageable);

    /**
     * Update an existing client.
     *
     * @param id the client ID
     * @param requestDTO the updated client data
     * @return the updated client
     */
    ClientResponseDTO updateClient(Long id, ClientRequestDTO requestDTO);

    /**
     * Deactivate a client (soft delete).
     *
     * @param id the client ID
     */
    void deactivateClient(Long id);

    /**
     * Activate a client.
     *
     * @param id the client ID
     */
    void activateClient(Long id);

    /**
     * Delete a client permanently.
     *
     * @param id the client ID
     */
    void deleteClient(Long id);

    /**
     * Get total count of active clients.
     *
     * @return the count of active clients
     */
    long getActiveClientCount();
}
