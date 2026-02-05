package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.dto.response.ApiResponseDTO;
import com.demo.MoneyMap.dto.response.TransactionResponseDTO;
import com.demo.MoneyMap.dto.response.AvailableAssetDTO;
import com.demo.MoneyMap.entity.enums.AssetType;
import com.demo.MoneyMap.entity.enums.AvailableAsset;
import com.demo.MoneyMap.service.AssetTradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Asset Trading operations.
 * Provides endpoints for buying and selling assets with wallet integration.
 */
@RestController
@RequestMapping("/api/v1/trading")
@RequiredArgsConstructor
@Tag(name = "Asset Trading", description = "APIs for buying and selling assets with wallet integration")
@Validated
public class AssetTradingController {

    private final AssetTradingService assetTradingService;

    @PostMapping("/buy")
    @Operation(
            summary = "Buy an asset",
            description = "Purchase an asset using client wallet funds. Creates a BUY transaction and updates asset holdings."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asset purchased successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds"),
            @ApiResponse(responseCode = "404", description = "Client or asset not found")
    })
    public ResponseEntity<ApiResponseDTO<TransactionResponseDTO>> buyAsset(
            @Parameter(description = "Client ID", required = true)
            @RequestParam @NotNull Long clientId,
            
            @Parameter(description = "Asset symbol to buy", required = true)
            @RequestParam @NotBlank String symbol,
            
            @Parameter(description = "Quantity to buy", required = true)
            @RequestParam @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal quantity,
            
            @Parameter(description = "Price per unit", required = true)
            @RequestParam @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price) {
        
        TransactionResponseDTO transaction = assetTradingService.buyAsset(clientId, symbol, quantity, price);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(transaction, "Asset purchased successfully"));
    }

    @PostMapping("/sell")
    @Operation(
            summary = "Sell an asset",
            description = "Sell owned assets and add proceeds to client wallet. Creates a SELL transaction."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asset sold successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient asset quantity"),
            @ApiResponse(responseCode = "404", description = "Client or asset not found")
    })
    public ResponseEntity<ApiResponseDTO<TransactionResponseDTO>> sellAsset(
            @Parameter(description = "Client ID", required = true)
            @RequestParam @NotNull Long clientId,
            
            @Parameter(description = "Asset ID to sell", required = true)
            @RequestParam @NotNull Long assetId,
            
            @Parameter(description = "Quantity to sell", required = true)
            @RequestParam @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal quantity,
            
            @Parameter(description = "Price per unit", required = true)
            @RequestParam @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price) {
        
        TransactionResponseDTO transaction = assetTradingService.sellAsset(clientId, assetId, quantity, price);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(transaction, "Asset sold successfully"));
    }

    @GetMapping("/assets/available")
    @Operation(
            summary = "Get all available assets for trading",
            description = "Returns a list of all assets that can be bought or sold"
    )
    @ApiResponse(responseCode = "200", description = "Available assets retrieved successfully")
    public ResponseEntity<ApiResponseDTO<List<AvailableAssetDTO>>> getAvailableAssets() {
        List<AvailableAssetDTO> assets = assetTradingService.getAvailableAssets();
        return ResponseEntity.ok(ApiResponseDTO.success(assets));
    }

    @GetMapping("/assets/available/type/{assetType}")
    @Operation(
            summary = "Get available assets by type",
            description = "Returns a list of available assets filtered by type (STOCK, CRYPTO, GOLD, MUTUAL_FUND)"
    )
    @ApiResponse(responseCode = "200", description = "Available assets retrieved successfully")
    public ResponseEntity<ApiResponseDTO<List<AvailableAssetDTO>>> getAvailableAssetsByType(
            @Parameter(description = "Asset type filter", required = true)
            @PathVariable AssetType assetType) {
        List<AvailableAssetDTO> assets = assetTradingService.getAvailableAssetsByType(assetType);
        return ResponseEntity.ok(ApiResponseDTO.success(assets));
    }

    @GetMapping("/assets/available/search")
    @Operation(
            summary = "Search available assets",
            description = "Search for available assets by name or symbol"
    )
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<ApiResponseDTO<List<AvailableAssetDTO>>> searchAvailableAssets(
            @Parameter(description = "Search query (name or symbol)", required = true)
            @RequestParam @NotBlank String query) {
        List<AvailableAssetDTO> assets = assetTradingService.searchAvailableAssets(query);
        return ResponseEntity.ok(ApiResponseDTO.success(assets));
    }

    @GetMapping("/wallet/{clientId}")
    @Operation(
            summary = "Get client wallet balance",
            description = "Returns the current wallet balance for a specific client"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<BigDecimal>> getWalletBalance(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long clientId) {
        BigDecimal balance = assetTradingService.getClientWalletBalance(clientId);
        return ResponseEntity.ok(ApiResponseDTO.success(balance));
    }

    @PostMapping("/wallet/{clientId}/deposit")
    @Operation(
            summary = "Add funds to client wallet",
            description = "Deposit funds into client's wallet"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funds added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<String>> addToWallet(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long clientId,
            
            @Parameter(description = "Amount to deposit", required = true)
            @RequestParam @NotNull @DecimalMin(value = "0.01") BigDecimal amount) {
        
        assetTradingService.addToWallet(clientId, amount);
        return ResponseEntity.ok(ApiResponseDTO.success("Funds added successfully"));
    }

    @GetMapping("/wallet/{clientId}/check")
    @Operation(
            summary = "Check if client has sufficient funds",
            description = "Returns true if client has sufficient funds for the given amount"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check completed successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<Boolean>> checkSufficientFunds(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long clientId,
            
            @Parameter(description = "Amount to check", required = true)
            @RequestParam @NotNull @DecimalMin(value = "0.01") BigDecimal amount) {
        
        boolean hasFunds = assetTradingService.hasSufficientFunds(clientId, amount);
        return ResponseEntity.ok(ApiResponseDTO.success(hasFunds));
    }
}
