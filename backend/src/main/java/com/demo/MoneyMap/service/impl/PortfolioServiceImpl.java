package com.demo.MoneyMap.service.impl;

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
import com.demo.MoneyMap.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of PortfolioService.
 * Enforces one-to-one relationship: each client can have exactly ONE portfolio.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ClientRepository clientRepository;
    private final AssetRepository assetRepository;
    private final PortfolioMapper portfolioMapper;

    @Override
    public PortfolioResponseDTO createPortfolio(PortfolioRequestDTO requestDTO) {
        log.info("Creating new portfolio: {} for client ID: {}", requestDTO.getName(), requestDTO.getClientId());

        Client client = clientRepository.findById(requestDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + requestDTO.getClientId()));

        // CRITICAL: Check if client already has a portfolio (one-to-one relationship)
        if (portfolioRepository.existsByClientId(requestDTO.getClientId())) {
            throw new DuplicateResourceException(
                    "Client already has a portfolio. Each client can only have one portfolio.");
        }

        Portfolio portfolio = portfolioMapper.toEntity(requestDTO);
        portfolio.setClient(client);

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        log.info("Successfully created portfolio with ID: {}", savedPortfolio.getId());
        return portfolioMapper.toResponseDTO(savedPortfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponseDTO getPortfolioById(Long id) {
        log.debug("Fetching portfolio with ID: {}", id);
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));
        return portfolioMapper.toResponseDTO(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponseDTO getPortfolioByIdWithAssets(Long id) {
        log.debug("Fetching portfolio with assets for ID: {}", id);
        Portfolio portfolio = portfolioRepository.findByIdWithAssets(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));
        return portfolioMapper.toResponseDTOWithAssets(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<PortfolioResponseDTO> getAllPortfolios(Pageable pageable) {
        log.debug("Fetching all portfolios with pagination");
        Page<Portfolio> page = portfolioRepository.findAll(pageable);
        return PagedResponseDTO.from(page, portfolioMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioResponseDTO> getPortfoliosByClientId(Long clientId) {
        log.debug("Fetching portfolios for client ID: {}", clientId);
        return portfolioRepository.findByClientId(clientId).stream()
                .map(portfolioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioResponseDTO> getActivePortfoliosByClientId(Long clientId) {
        log.debug("Fetching active portfolios for client ID: {}", clientId);
        return portfolioRepository.findByClientIdAndActiveTrue(clientId).stream()
                .map(portfolioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<PortfolioResponseDTO> searchPortfolios(String query, Pageable pageable) {
        log.debug("Searching portfolios with query: {}", query);
        Page<Portfolio> page = portfolioRepository.searchPortfolios(query, pageable);
        return PagedResponseDTO.from(page, portfolioMapper::toResponseDTO);
    }

    @Override
    public PortfolioResponseDTO updatePortfolio(Long id, PortfolioRequestDTO requestDTO) {
        log.info("Updating portfolio with ID: {}", id);

        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));

        // If client changed, verify new client exists and doesn't have a portfolio
        if (!portfolio.getClient().getId().equals(requestDTO.getClientId())) {
            Client newClient = clientRepository.findById(requestDTO.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + requestDTO.getClientId()));

            if (portfolioRepository.existsByClientId(requestDTO.getClientId())) {
                throw new DuplicateResourceException(
                        "Target client already has a portfolio. Each client can only have one portfolio.");
            }

            portfolio.setClient(newClient);
        }

        portfolioMapper.updateEntityFromDTO(requestDTO, portfolio);
        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);

        log.info("Successfully updated portfolio with ID: {}", id);
        return portfolioMapper.toResponseDTO(updatedPortfolio);
    }

    @Override
    public PortfolioResponseDTO recalculateTotalValue(Long id) {
        log.info("Recalculating value for portfolio ID: {}", id);

        Portfolio portfolio = portfolioRepository.findByIdWithAssets(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));

        portfolio.recalculateTotalValue();
        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);

        log.info("Successfully recalculated portfolio value. New value: {}", updatedPortfolio.getTotalValue());
        return portfolioMapper.toResponseDTO(updatedPortfolio);
    }

    @Override
    public void deletePortfolio(Long id) {
        log.info("Deleting portfolio with ID: {}", id);

        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));

        portfolioRepository.delete(portfolio);

        log.info("Successfully deleted portfolio with ID: {}", id);
    }

    @Override
    public void deactivatePortfolio(Long id) {
        log.info("Deactivating portfolio with ID: {}", id);

        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));

        portfolio.setActive(false);
        portfolioRepository.save(portfolio);

        log.info("Successfully deactivated portfolio with ID: {}", id);
    }

    @Override
    public void activatePortfolio(Long id) {
        log.info("Activating portfolio with ID: {}", id);

        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + id));

        portfolio.setActive(true);
        portfolioRepository.save(portfolio);

        log.info("Successfully activated portfolio with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalValueByClientId(Long clientId) {
        log.debug("Calculating total portfolio value for client ID: {}", clientId);
        return portfolioRepository.getTotalValueByClientId(clientId);
    }
}
