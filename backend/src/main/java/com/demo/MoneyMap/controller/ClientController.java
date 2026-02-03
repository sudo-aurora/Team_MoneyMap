package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.dto.request.ClientRequestDTO;
import com.demo.MoneyMap.dto.response.ApiResponseDTO;
import com.demo.MoneyMap.dto.response.ClientResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.service.ClientService;
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

import java.util.List;

/**
 * REST Controller for Client management operations.
 * Provides endpoints for CRUD operations on clients.
 */
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Client Management", description = "APIs for managing clients of the portfolio management system. " +
        "Supports creating, reading, updating, and deleting client records.")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @Operation(
            summary = "Create a new client",
            description = "Creates a new client in the system. Email must be unique across all clients."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Client with this email already exists")
    })
    public ResponseEntity<ApiResponseDTO<ClientResponseDTO>> createClient(
            @Valid @RequestBody ClientRequestDTO requestDTO) {
        ClientResponseDTO client = clientService.createClient(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(client, "Client created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get client by ID",
            description = "Retrieves a client's details by their unique identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client found"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<ClientResponseDTO>> getClientById(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(client));
    }

    @GetMapping("/{id}/portfolios")
    @Operation(
            summary = "Get client with portfolios",
            description = "Retrieves a client's details including all their portfolio summaries."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client found with portfolios"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<ClientResponseDTO>> getClientWithPortfolios(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long id) {
        ClientResponseDTO client = clientService.getClientByIdWithPortfolios(id);
        return ResponseEntity.ok(ApiResponseDTO.success(client));
    }

    @GetMapping
    @Operation(
            summary = "Get all clients",
            description = "Retrieves a paginated list of all clients. Supports sorting by any field."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved client list")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<ClientResponseDTO>>> getAllClients(
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
        PagedResponseDTO<ClientResponseDTO> clients = clientService.getAllClients(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(clients));
    }

    @GetMapping("/active")
    @Operation(
            summary = "Get all active clients",
            description = "Retrieves a list of all active (non-deactivated) clients."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active client list")
    public ResponseEntity<ApiResponseDTO<List<ClientResponseDTO>>> getActiveClients() {
        List<ClientResponseDTO> clients = clientService.getActiveClients();
        return ResponseEntity.ok(ApiResponseDTO.success(clients));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search clients",
            description = "Searches for clients by first name, last name, or email. Supports pagination."
    )
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<ClientResponseDTO>>> searchClients(
            @Parameter(description = "Search term", required = true)
            @RequestParam String query,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponseDTO<ClientResponseDTO> clients = clientService.searchClients(query, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(clients));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a client",
            description = "Updates an existing client's information. Email must remain unique."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "409", description = "Email already in use by another client")
    })
    public ResponseEntity<ApiResponseDTO<ClientResponseDTO>> updateClient(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDTO requestDTO) {
        ClientResponseDTO client = clientService.updateClient(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(client, "Client updated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate a client",
            description = "Soft deletes a client by setting their status to inactive. " +
                    "The client and their data are preserved but marked as inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deactivateClient(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long id) {
        clientService.deactivateClient(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Client deactivated successfully"));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate a client",
            description = "Reactivates a previously deactivated client."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client activated successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<Void>> activateClient(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long id) {
        clientService.activateClient(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Client activated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a client",
            description = "Permanently deletes a client and all associated data (portfolios, assets, transactions). " +
                    "This action cannot be undone. Consider deactivating instead."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteClient(
            @Parameter(description = "Client ID", required = true)
            @PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Client deleted successfully"));
    }

    @GetMapping("/count/active")
    @Operation(
            summary = "Get active client count",
            description = "Returns the total number of active clients in the system."
    )
    @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    public ResponseEntity<ApiResponseDTO<Long>> getActiveClientCount() {
        long count = clientService.getActiveClientCount();
        return ResponseEntity.ok(ApiResponseDTO.success(count));
    }
}
