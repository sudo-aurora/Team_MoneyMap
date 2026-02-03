package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.dto.request.PaymentRequestDTO;
import com.demo.MoneyMap.dto.request.PaymentStatusUpdateDTO;
import com.demo.MoneyMap.dto.response.ApiResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.dto.response.PaymentResponseDTO;
import com.demo.MoneyMap.dto.response.PaymentStatusHistoryDTO;
import com.demo.MoneyMap.entity.enums.PaymentStatus;
import com.demo.MoneyMap.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * REST Controller for Payment management operations.
 * Handles the complete payment lifecycle from creation through completion.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Processing", description = "APIs for managing payments through their lifecycle. " +
        "Supports creating, validating, sending, and completing payments with full status history tracking.")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Creates a new payment and starts the payment lifecycle. " +
            "Uses idempotency key to prevent duplicate payments. Payment starts in CREATED status.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment data"),
            @ApiResponse(responseCode = "409", description = "Duplicate payment (same idempotency key)")
    })
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> createPayment(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        PaymentResponseDTO payment = paymentService.createPayment(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(payment, "Payment created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment details by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(payment));
    }

    @GetMapping("/reference/{paymentReference}")
    @Operation(summary = "Get payment by reference", description = "Retrieves payment details by payment reference number.")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> getPaymentByReference(@PathVariable String paymentReference) {
        PaymentResponseDTO payment = paymentService.getPaymentByReference(paymentReference);
        return ResponseEntity.ok(ApiResponseDTO.success(payment));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get payment status history", description = "Retrieves the complete status change history for a payment. " +
            "Shows all status transitions with timestamps and notes.")
    public ResponseEntity<ApiResponseDTO<List<PaymentStatusHistoryDTO>>> getPaymentHistory(@PathVariable Long id) {
        List<PaymentStatusHistoryDTO> history = paymentService.getPaymentHistory(id);
        return ResponseEntity.ok(ApiResponseDTO.success(history));
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Get payment with full history", description = "Retrieves payment details including complete status history.")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> getPaymentWithHistory(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.getPaymentWithHistory(id);
        return ResponseEntity.ok(ApiResponseDTO.success(payment));
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves a paginated list of all payments with sorting support.")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<PaymentResponseDTO>>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponseDTO<PaymentResponseDTO> payments = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(payments));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Retrieves payments filtered by their current status. " +
            "Valid statuses: CREATED, VALIDATED, SENT, COMPLETED, FAILED")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<PaymentResponseDTO>>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponseDTO<PaymentResponseDTO> payments = paymentService.getPaymentsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(payments));
    }

    @GetMapping("/account/{sourceAccount}")
    @Operation(summary = "Get payments by source account", description = "Retrieves all payments from a specific source account.")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<PaymentResponseDTO>>> getPaymentsBySourceAccount(
            @PathVariable String sourceAccount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponseDTO<PaymentResponseDTO> payments = paymentService.getPaymentsBySourceAccount(sourceAccount, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(payments));
    }

    @GetMapping("/search")
    @Operation(summary = "Search payments", description = "Searches payments by reference or description.")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<PaymentResponseDTO>>> searchPayments(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponseDTO<PaymentResponseDTO> payments = paymentService.searchPayments(query, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(payments));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update payment status", description = "Updates the status of a payment. Validates that the transition is allowed. " +
            "Valid transitions: CREATED→VALIDATED, VALIDATED→SENT, SENT→COMPLETED, any→FAILED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> updatePaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody PaymentStatusUpdateDTO updateDTO) {
        PaymentResponseDTO payment = paymentService.updatePaymentStatus(id, updateDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(payment, "Payment status updated successfully"));
    }

    @PostMapping("/{id}/validate")
    @Operation(summary = "Validate payment", description = "Moves payment from CREATED to VALIDATED status. " +
            "Simulates validation checks passing.")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> validatePayment(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.validatePayment(id);
        return ResponseEntity.ok(ApiResponseDTO.success(payment, "Payment validated successfully"));
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "Send payment", description = "Moves payment from VALIDATED to SENT status. " +
            "Simulates transmitting the payment to destination system.")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> sendPayment(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.sendPayment(id);
        return ResponseEntity.ok(ApiResponseDTO.success(payment, "Payment sent successfully"));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete payment", description = "Moves payment from SENT to COMPLETED status. " +
            "Marks the payment as successfully processed.")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> completePayment(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.completePayment(id);
        return ResponseEntity.ok(ApiResponseDTO.success(payment, "Payment completed successfully"));
    }

    @PostMapping("/{id}/fail")
    @Operation(summary = "Fail payment", description = "Marks a payment as FAILED with error details. " +
            "Can be called from any non-terminal status.")
    public ResponseEntity<ApiResponseDTO<PaymentResponseDTO>> failPayment(
            @PathVariable Long id,
            @RequestParam String errorCode,
            @RequestParam(required = false) String errorMessage) {
        PaymentResponseDTO payment = paymentService.failPayment(id, errorCode, errorMessage);
        return ResponseEntity.ok(ApiResponseDTO.success(payment, "Payment marked as failed"));
    }

    @GetMapping("/count/{status}")
    @Operation(summary = "Get payment count by status", description = "Returns the count of payments in a specific status.")
    public ResponseEntity<ApiResponseDTO<Long>> getPaymentCountByStatus(@PathVariable PaymentStatus status) {
        long count = paymentService.getPaymentCountByStatus(status);
        return ResponseEntity.ok(ApiResponseDTO.success(count));
    }
}
