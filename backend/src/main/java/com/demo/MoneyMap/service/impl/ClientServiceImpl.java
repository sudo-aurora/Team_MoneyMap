package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.dto.request.ClientRequestDTO;
import com.demo.MoneyMap.dto.response.ClientResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.Client;
import com.demo.MoneyMap.exception.DuplicateResourceException;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.mapper.ClientMapper;
import com.demo.MoneyMap.repository.ClientRepository;
import com.demo.MoneyMap.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ClientService.
 * Follows Single Responsibility Principle - handles only client-related business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO requestDTO) {
        log.info("Creating new client with email: {}", requestDTO.getEmail());

        // Check for duplicate email
        if (clientRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Client with email " + requestDTO.getEmail() + " already exists");
        }

        Client client = clientMapper.toEntity(requestDTO);
        Client savedClient = clientRepository.save(client);

        log.info("Successfully created client with ID: {}", savedClient.getId());
        return clientMapper.toResponseDTO(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO getClientById(Long id) {
        log.debug("Fetching client with ID: {}", id);
        Client client = findClientById(id);
        return clientMapper.toResponseDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO getClientByIdWithPortfolios(Long id) {
        log.debug("Fetching client with portfolios for ID: {}", id);
        Client client = clientRepository.findByIdWithPortfolios(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));
        return clientMapper.toResponseDTOWithPortfolios(client);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<ClientResponseDTO> getAllClients(Pageable pageable) {
        log.debug("Fetching all clients with pagination");
        Page<Client> clientPage = clientRepository.findAll(pageable);
        return mapToPagedResponse(clientPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> getActiveClients() {
        log.debug("Fetching all active clients");
        return clientRepository.findByActiveTrue().stream()
                .map(clientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<ClientResponseDTO> searchClients(String searchTerm, Pageable pageable) {
        log.debug("Searching clients with term: {}", searchTerm);
        Page<Client> clientPage = clientRepository.searchClients(searchTerm, pageable);
        return mapToPagedResponse(clientPage);
    }

    @Override
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO requestDTO) {
        log.info("Updating client with ID: {}", id);

        Client client = findClientById(id);

        // Check for duplicate email (excluding current client)
        if (clientRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
            throw new DuplicateResourceException("Client with email " + requestDTO.getEmail() + " already exists");
        }

        clientMapper.updateEntityFromDTO(requestDTO, client);
        Client updatedClient = clientRepository.save(client);

        log.info("Successfully updated client with ID: {}", id);
        return clientMapper.toResponseDTO(updatedClient);
    }

    @Override
    public void deactivateClient(Long id) {
        log.info("Deactivating client with ID: {}", id);
        Client client = findClientById(id);
        client.setActive(false);
        clientRepository.save(client);
        log.info("Successfully deactivated client with ID: {}", id);
    }

    @Override
    public void activateClient(Long id) {
        log.info("Activating client with ID: {}", id);
        Client client = findClientById(id);
        client.setActive(true);
        clientRepository.save(client);
        log.info("Successfully activated client with ID: {}", id);
    }

    @Override
    public void deleteClient(Long id) {
        log.info("Deleting client with ID: {}", id);
        Client client = findClientById(id);
        clientRepository.delete(client);
        log.info("Successfully deleted client with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveClientCount() {
        return clientRepository.countByActiveTrue();
    }

    /**
     * Helper method to find client by ID or throw exception.
     */
    private Client findClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));
    }

    /**
     * Helper method to map Page to PagedResponseDTO.
     */
    private PagedResponseDTO<ClientResponseDTO> mapToPagedResponse(Page<Client> page) {
        List<ClientResponseDTO> content = page.getContent().stream()
                .map(clientMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PagedResponseDTO.<ClientResponseDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
