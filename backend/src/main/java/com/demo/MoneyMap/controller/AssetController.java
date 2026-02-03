package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.dto.request.AssetRequestDTO;
import com.demo.MoneyMap.dto.response.ApiResponseDTO;
import com.demo.MoneyMap.dto.response.AssetResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.enums.AssetType;
import com.demo.MoneyMap.service.AssetService;
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
 * REST Controller for Asset management operations.
 * Provides endpoints for CRUD operations on assets (Gold, Stocks, Mutual Funds, Crypto).
 */
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Management", description = "APIs for managing investment assets. " +
        "Supports Gold, Stocks, Mutual Funds, and Cryptocurrencies. " +
        "Each asset belongs to a portfolio and tracks quantity, purchase price, and current value.")
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @Operation(
            summary = "Create a new asset",
            description = "Creates a new asset in a portfolio. Supported asset types: GOLD, STOCK, MUTUAL_FUND, CRYPTO. " +
                    "The current value is automatically calculated based on quantity and price."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asset created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<AssetResponseDTO>> createAsset(
            @Valid @RequestBody AssetRequestDTO requestDTO) {
        AssetResponseDTO asset = assetService.createAsset(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(asset, "Asset created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get asset by ID",
            description = "Retrieves an asset's details by its unique identifier, including profit/loss calculations."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset found"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<ApiResponseDTO<AssetResponseDTO>> getAssetById(
            @Parameter(description = "Asset ID", required = true)
            @PathVariable Long id) {
        AssetResponseDTO asset = assetService.getAssetById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(asset));
    }

    @GetMapping("/{id}/transactions")
    @Operation(
            summary = "Get asset with transactions",
            description = "Retrieves an asset's details including its transaction history."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset found with transactions"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<ApiResponseDTO<AssetResponseDTO>> getAssetWithTransactions(
            @Parameter(description = "Asset ID", required = true)
            @PathVariable Long id) {
        AssetResponseDTO asset = assetService.getAssetByIdWithTransactions(id);
        return ResponseEntity.ok(ApiResponseDTO.success(asset));
    }

    @GetMapping
    @Operation(
            summary = "Get all assets",
            description = "Retrieves a paginated list of all assets across all portfolios."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved asset list")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<AssetResponseDTO>>> getAllAssets(
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
        PagedResponseDTO<AssetResponseDTO> assets = assetService.getAllAssets(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(assets));
    }

    @GetMapping("/portfolio/{portfolioId}")
    @Operation(
            summary = "Get assets by portfolio",
            description = "Retrieves all assets belonging to a specific portfolio."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved assets"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<List<AssetResponseDTO>>> getAssetsByPortfolioId(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long portfolioId) {
        List<AssetResponseDTO> assets = assetService.getAssetsByPortfolioId(portfolioId);
        return ResponseEntity.ok(ApiResponseDTO.success(assets));
    }

    @GetMapping("/portfolio/{portfolioId}/paged")
    @Operation(
            summary = "Get assets by portfolio (paginated)",
            description = "Retrieves a paginated list of assets belonging to a specific portfolio."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved assets"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<AssetResponseDTO>>> getAssetsByPortfolioIdPaged(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long portfolioId,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponseDTO<AssetResponseDTO> assets = assetService.getAssetsByPortfolioId(portfolioId, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(assets));
    }

    @GetMapping("/type/{assetType}")
    @Operation(
            summary = "Get assets by type",
            description = "Retrieves all assets of a specific type (GOLD, STOCK, MUTUAL_FUND, CRYPTO)."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved assets")
    public ResponseEntity<ApiResponseDTO<List<AssetResponseDTO>>> getAssetsByType(
            @Parameter(description = "Asset type", required = true)
            @PathVariable AssetType assetType) {
        List<AssetResponseDTO> assets = assetService.getAssetsByType(assetType);
        return ResponseEntity.ok(ApiResponseDTO.success(assets));
    }

    @GetMapping("/type/{assetType}/paged")
    @Operation(
            summary = "Get assets by type (paginated)",
            description = "Retrieves a paginated list of assets of a specific type."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved assets")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<AssetResponseDTO>>> getAssetsByTypePaged(
            @Parameter(description = "Asset type", required = true)
            @PathVariable AssetType assetType,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponseDTO<AssetResponseDTO> assets = assetService.getAssetsByType(assetType, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(assets));
    }

    @GetMapping("/client/{clientId}")
    @Operation(
            summary = "Get assets by client",
            description = "Retrieves all assets across all portfolios belonging to a specific client."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved assets")
    public ResponseEntity<ApiResponseDTO<List<AssetResponseDTO>>> getAssetsByClientId(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long clientId) {
        List<AssetResponseDTO> assets = assetService.getAssetsByClientId(clientId);
        return ResponseEntity.ok(ApiResponseDTO.success(assets));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search assets",
            description = "Searches for assets by name or symbol. Supports pagination."
    )
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<AssetResponseDTO>>> searchAssets(
            @Parameter(description = "Search term (name or symbol)", required = true)
            @RequestParam String query,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponseDTO<AssetResponseDTO> assets = assetService.searchAssets(query, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(assets));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an asset",
            description = "Updates an existing asset's information. Can also move to a different portfolio."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Asset or Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<AssetResponseDTO>> updateAsset(
            @Parameter(description = "Asset ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody AssetRequestDTO requestDTO) {
        AssetResponseDTO asset = assetService.updateAsset(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(asset, "Asset updated successfully"));
    }

    @PatchMapping("/{id}/price")
    @Operation(
            summary = "Update asset price",
            description = "Updates the current market price of an asset. " +
                    "This automatically recalculates the current value and profit/loss."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset price updated successfully"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<ApiResponseDTO<AssetResponseDTO>> updateAssetPrice(
            @Parameter(description = "Asset ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New current price", required = true)
            @RequestParam BigDecimal currentPrice) {
        AssetResponseDTO asset = assetService.updateAssetPrice(id, currentPrice);
        return ResponseEntity.ok(ApiResponseDTO.success(asset, "Asset price updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an asset",
            description = "Permanently deletes an asset and all its transaction history. " +
                    "This action cannot be undone."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteAsset(
            @Parameter(description = "Asset ID", required = true)
            @PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Asset deleted successfully"));
    }

    @GetMapping("/type/{assetType}/total-value")
    @Operation(
            summary = "Get total value by asset type",
            description = "Calculates and returns the total value of all assets of a specific type across all portfolios."
    )
    @ApiResponse(responseCode = "200", description = "Total value calculated successfully")
    public ResponseEntity<ApiResponseDTO<BigDecimal>> getTotalValueByAssetType(
            @Parameter(description = "Asset type", required = true)
            @PathVariable AssetType assetType) {
        BigDecimal totalValue = assetService.getTotalValueByAssetType(assetType);
        return ResponseEntity.ok(ApiResponseDTO.success(totalValue));
    }

    @GetMapping("/types")
    @Operation(
            summary = "Get all asset types",
            description = "Returns a list of all supported asset types: GOLD, STOCK, MUTUAL_FUND, CRYPTO."
    )
    @ApiResponse(responseCode = "200", description = "Asset types retrieved successfully")
    public ResponseEntity<ApiResponseDTO<List<AssetType>>> getAssetTypes() {
        List<AssetType> assetTypes = assetService.getAssetTypes();
        return ResponseEntity.ok(ApiResponseDTO.success(assetTypes));
    }
}
