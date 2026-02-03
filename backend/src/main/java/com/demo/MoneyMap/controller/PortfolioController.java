package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.dto.request.PortfolioRequestDTO;
import com.demo.MoneyMap.dto.response.ApiResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.dto.response.PortfolioResponseDTO;
import com.demo.MoneyMap.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Portfolio management operations.
 * Provides endpoints for CRUD operations on portfolios.
 */
@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@Tag(name = "Portfolio Management", description = "APIs for managing investment portfolios. " +
        "Each portfolio belongs to a client and contains multiple assets.")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    @Operation(
            summary = "Create a new portfolio",
            description = "Creates a new investment portfolio for an existing client. " +
                    "A client can have multiple portfolios."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Portfolio created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<PortfolioResponseDTO>> createPortfolio(
            @Valid @RequestBody PortfolioRequestDTO requestDTO) {
        PortfolioResponseDTO portfolio = portfolioService.createPortfolio(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(portfolio, "Portfolio created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get portfolio by ID",
            description = "Retrieves a portfolio's details by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfolio found"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<PortfolioResponseDTO>> getPortfolioById(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long id) {
        PortfolioResponseDTO portfolio = portfolioService.getPortfolioById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(portfolio));
    }

    @GetMapping("/{id}/assets")
    @Operation(
            summary = "Get portfolio with assets",
            description = "Retrieves a portfolio's details including all asset summaries."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfolio found with assets"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<PortfolioResponseDTO>> getPortfolioWithAssets(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long id) {
        PortfolioResponseDTO portfolio = portfolioService.getPortfolioByIdWithAssets(id);
        return ResponseEntity.ok(ApiResponseDTO.success(portfolio));
    }

    @GetMapping
    @Operation(
            summary = "Get all portfolios",
            description = "Retrieves a paginated list of all portfolios. Supports sorting by any field."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved portfolio list")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<PortfolioResponseDTO>>> getAllPortfolios(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponseDTO<PortfolioResponseDTO> portfolios = portfolioService.getAllPortfolios(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(portfolios));
    }

    @GetMapping("/client/{clientId}")
    @Operation(
            summary = "Get portfolios by client",
            description = "Retrieves all portfolios belonging to a specific client."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved portfolios"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<List<PortfolioResponseDTO>>> getPortfoliosByClientId(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long clientId) {
        List<PortfolioResponseDTO> portfolios = portfolioService.getPortfoliosByClientId(clientId);
        return ResponseEntity.ok(ApiResponseDTO.success(portfolios));
    }

    @GetMapping("/client/{clientId}/active")
    @Operation(
            summary = "Get active portfolios by client",
            description = "Retrieves all active portfolios belonging to a specific client."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active portfolios"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<List<PortfolioResponseDTO>>> getActivePortfoliosByClientId(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long clientId) {
        List<PortfolioResponseDTO> portfolios = portfolioService.getActivePortfoliosByClientId(clientId);
        return ResponseEntity.ok(ApiResponseDTO.success(portfolios));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search portfolios",
            description = "Searches for portfolios by name. Supports pagination."
    )
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<PortfolioResponseDTO>>> searchPortfolios(
            @Parameter(description = "Search term", required = true)
            @RequestParam String query,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponseDTO<PortfolioResponseDTO> portfolios = portfolioService.searchPortfolios(query, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(portfolios));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a portfolio",
            description = "Updates an existing portfolio's information. Can also transfer to a different client."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfolio updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Portfolio or Client not found")
    })
    public ResponseEntity<ApiResponseDTO<PortfolioResponseDTO>> updatePortfolio(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody PortfolioRequestDTO requestDTO) {
        PortfolioResponseDTO portfolio = portfolioService.updatePortfolio(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(portfolio, "Portfolio updated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate a portfolio",
            description = "Soft deletes a portfolio by setting its status to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfolio deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deactivatePortfolio(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long id) {
        portfolioService.deactivatePortfolio(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Portfolio deactivated successfully"));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate a portfolio",
            description = "Reactivates a previously deactivated portfolio."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfolio activated successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<Void>> activatePortfolio(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long id) {
        portfolioService.activatePortfolio(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Portfolio activated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a portfolio",
            description = "Permanently deletes a portfolio and all its assets and transactions. " +
                    "This action cannot be undone."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfolio deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deletePortfolio(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Portfolio deleted successfully"));
    }

    @GetMapping("/client/{clientId}/total-value")
    @Operation(
            summary = "Get total portfolio value for client",
            description = "Calculates and returns the total value of all active portfolios for a client."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total value calculated successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<BigDecimal>> getTotalValueByClientId(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long clientId) {
        BigDecimal totalValue = portfolioService.getTotalValueByClientId(clientId);
        return ResponseEntity.ok(ApiResponseDTO.success(totalValue));
    }

    @PostMapping("/{id}/recalculate")
    @Operation(
            summary = "Recalculate portfolio value",
            description = "Recalculates the total value of a portfolio based on current asset values."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfolio value recalculated successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<PortfolioResponseDTO>> recalculateTotalValue(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long id) {
        PortfolioResponseDTO portfolio = portfolioService.recalculateTotalValue(id);
        return ResponseEntity.ok(ApiResponseDTO.success(portfolio, "Portfolio value recalculated successfully"));
    }
}
