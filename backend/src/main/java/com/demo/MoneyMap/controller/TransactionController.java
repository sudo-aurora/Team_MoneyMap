package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.dto.request.TransactionRequestDTO;
import com.demo.MoneyMap.dto.response.ApiResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.dto.response.TransactionResponseDTO;
import com.demo.MoneyMap.entity.enums.TransactionType;
import com.demo.MoneyMap.service.TransactionService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Transaction management operations.
 * Provides endpoints for recording and managing asset transactions.
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "APIs for managing asset transactions. " +
        "Supports recording BUY, SELL, DIVIDEND, INTEREST, TRANSFER_IN, and TRANSFER_OUT transactions. " +
        "Transactions automatically update the asset's quantity and portfolio's total value.")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(
            summary = "Create a new transaction",
            description = "Records a new transaction for an asset. BUY and TRANSFER_IN increase quantity, " +
                    "SELL and TRANSFER_OUT decrease quantity. DIVIDEND and INTEREST do not affect quantity. " +
                    "The portfolio's total value is automatically recalculated."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<ApiResponseDTO<TransactionResponseDTO>> createTransaction(
            @Valid @RequestBody TransactionRequestDTO requestDTO) {
        TransactionResponseDTO transaction = transactionService.createTransaction(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(transaction, "Transaction recorded successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves a transaction's details by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction found"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponseDTO<TransactionResponseDTO>> getTransactionById(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable Long id) {
        TransactionResponseDTO transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(transaction));
    }

    @GetMapping
    @Operation(
            summary = "Get all transactions",
            description = "Retrieves a paginated list of all transactions. Default sort is by transaction date descending."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved transaction list")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<TransactionResponseDTO>>> getAllTransactions(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponseDTO<TransactionResponseDTO> transactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(transactions));
    }

    @GetMapping("/asset/{assetId}")
    @Operation(
            summary = "Get transactions by asset",
            description = "Retrieves all transactions for a specific asset."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<ApiResponseDTO<List<TransactionResponseDTO>>> getTransactionsByAssetId(
            @Parameter(description = "Asset ID", required = true)
            @PathVariable Long assetId) {
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByAssetId(assetId);
        return ResponseEntity.ok(ApiResponseDTO.success(transactions));
    }

    @GetMapping("/asset/{assetId}/paged")
    @Operation(
            summary = "Get transactions by asset (paginated)",
            description = "Retrieves a paginated list of transactions for a specific asset."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<TransactionResponseDTO>>> getTransactionsByAssetIdPaged(
            @Parameter(description = "Asset ID", required = true)
            @PathVariable Long assetId,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        PagedResponseDTO<TransactionResponseDTO> transactions = transactionService.getTransactionsByAssetId(assetId, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(transactions));
    }

    @GetMapping("/portfolio/{portfolioId}")
    @Operation(
            summary = "Get transactions by portfolio",
            description = "Retrieves all transactions across all assets in a specific portfolio."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<List<TransactionResponseDTO>>> getTransactionsByPortfolioId(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long portfolioId) {
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByPortfolioId(portfolioId);
        return ResponseEntity.ok(ApiResponseDTO.success(transactions));
    }

    @GetMapping("/portfolio/{portfolioId}/paged")
    @Operation(
            summary = "Get transactions by portfolio (paginated)",
            description = "Retrieves a paginated list of transactions for a specific portfolio."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found")
    })
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<TransactionResponseDTO>>> getTransactionsByPortfolioIdPaged(
            @Parameter(description = "Portfolio ID", required = true)
            @PathVariable Long portfolioId,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        PagedResponseDTO<TransactionResponseDTO> transactions = transactionService.getTransactionsByPortfolioId(portfolioId, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(transactions));
    }

    @GetMapping("/client/{clientId}")
    @Operation(
            summary = "Get transactions by client",
            description = "Retrieves all transactions across all portfolios belonging to a specific client."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions")
    public ResponseEntity<ApiResponseDTO<List<TransactionResponseDTO>>> getTransactionsByClientId(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long clientId) {
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByClientId(clientId);
        return ResponseEntity.ok(ApiResponseDTO.success(transactions));
    }

    @GetMapping("/client/{clientId}/paged")
    @Operation(
            summary = "Get transactions by client (paginated)",
            description = "Retrieves a paginated list of transactions for a specific client."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<TransactionResponseDTO>>> getTransactionsByClientIdPaged(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long clientId,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        PagedResponseDTO<TransactionResponseDTO> transactions = transactionService.getTransactionsByClientId(clientId, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(transactions));
    }

    @GetMapping("/type/{transactionType}")
    @Operation(
            summary = "Get transactions by type",
            description = "Retrieves all transactions of a specific type (BUY, SELL, DIVIDEND, etc.)."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<TransactionResponseDTO>>> getTransactionsByType(
            @Parameter(description = "Transaction type", required = true)
            @PathVariable TransactionType transactionType,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        PagedResponseDTO<TransactionResponseDTO> transactions = transactionService.getTransactionsByType(transactionType, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(transactions));
    }

    @GetMapping("/date-range")
    @Operation(
            summary = "Get transactions by date range",
            description = "Retrieves all transactions within a specified date range."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<TransactionResponseDTO>>> getTransactionsByDateRange(
            @Parameter(description = "Start date (ISO format)", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        PagedResponseDTO<TransactionResponseDTO> transactions = transactionService.getTransactionsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(transactions));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a transaction",
            description = "Updates an existing transaction. The asset quantity and portfolio value are " +
                    "automatically adjusted to reflect the changes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Transaction or Asset not found")
    })
    public ResponseEntity<ApiResponseDTO<TransactionResponseDTO>> updateTransaction(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDTO requestDTO) {
        TransactionResponseDTO transaction = transactionService.updateTransaction(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(transaction, "Transaction updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a transaction",
            description = "Permanently deletes a transaction. The asset quantity is automatically " +
                    "adjusted (reversed) to undo the transaction's effect."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteTransaction(
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Transaction deleted successfully"));
    }

    @GetMapping("/types")
    @Operation(
            summary = "Get all transaction types",
            description = "Returns a list of all supported transaction types: BUY, SELL, DIVIDEND, INTEREST, TRANSFER_IN, TRANSFER_OUT."
    )
    @ApiResponse(responseCode = "200", description = "Transaction types retrieved successfully")
    public ResponseEntity<ApiResponseDTO<List<TransactionType>>> getTransactionTypes() {
        List<TransactionType> transactionTypes = transactionService.getTransactionTypes();
        return ResponseEntity.ok(ApiResponseDTO.success(transactionTypes));
    }
}
