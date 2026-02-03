package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.PortfolioRequestDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.dto.response.PortfolioResponseDTO;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for portfolio management operations.
 * Follows Interface Segregation Principle (ISP) from SOLID.
 */
public interface PortfolioService {

    /**
     * Create a new portfolio.
     *
     * @param requestDTO the portfolio data to create
     * @return the created portfolio
     */
    PortfolioResponseDTO createPortfolio(PortfolioRequestDTO requestDTO);

    /**
     * Get a portfolio by ID.
     *
     * @param id the portfolio ID
     * @return the portfolio details
     */
    PortfolioResponseDTO getPortfolioById(Long id);

    /**
     * Get a portfolio by ID with assets.
     *
     * @param id the portfolio ID
     * @return the portfolio details with assets
     */
    PortfolioResponseDTO getPortfolioByIdWithAssets(Long id);

    /**
     * Get all portfolios with pagination.
     *
     * @param pageable pagination parameters
     * @return paginated list of portfolios
     */
    PagedResponseDTO<PortfolioResponseDTO> getAllPortfolios(Pageable pageable);

    /**
     * Get all portfolios for a specific client.
     *
     * @param clientId the client ID
     * @return list of portfolios
     */
    List<PortfolioResponseDTO> getPortfoliosByClientId(Long clientId);

    /**
     * Get all active portfolios for a specific client.
     *
     * @param clientId the client ID
     * @return list of active portfolios
     */
    List<PortfolioResponseDTO> getActivePortfoliosByClientId(Long clientId);

    /**
     * Search portfolios by name.
     *
     * @param searchTerm the search term
     * @param pageable pagination parameters
     * @return paginated list of matching portfolios
     */
    PagedResponseDTO<PortfolioResponseDTO> searchPortfolios(String searchTerm, Pageable pageable);

    /**
     * Update an existing portfolio.
     *
     * @param id the portfolio ID
     * @param requestDTO the updated portfolio data
     * @return the updated portfolio
     */
    PortfolioResponseDTO updatePortfolio(Long id, PortfolioRequestDTO requestDTO);

    /**
     * Deactivate a portfolio (soft delete).
     *
     * @param id the portfolio ID
     */
    void deactivatePortfolio(Long id);

    /**
     * Activate a portfolio.
     *
     * @param id the portfolio ID
     */
    void activatePortfolio(Long id);

    /**
     * Delete a portfolio permanently.
     *
     * @param id the portfolio ID
     */
    void deletePortfolio(Long id);

    /**
     * Get total value of all portfolios for a client.
     *
     * @param clientId the client ID
     * @return total portfolio value
     */
    BigDecimal getTotalValueByClientId(Long clientId);

    /**
     * Recalculate portfolio total value.
     *
     * @param id the portfolio ID
     * @return updated portfolio
     */
    PortfolioResponseDTO recalculateTotalValue(Long id);
}
