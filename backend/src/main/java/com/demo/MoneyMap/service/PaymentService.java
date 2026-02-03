package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.PaymentRequestDTO;
import com.demo.MoneyMap.dto.request.PaymentStatusUpdateDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.dto.response.PaymentResponseDTO;
import com.demo.MoneyMap.dto.response.PaymentStatusHistoryDTO;
import com.demo.MoneyMap.entity.enums.PaymentStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for payment management operations.
 */
public interface PaymentService {

    /**
     * Create a new payment.
     */
    PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO);

    /**
     * Get payment by ID.
     */
    PaymentResponseDTO getPaymentById(Long id);

    /**
     * Get payment by payment reference.
     */
    PaymentResponseDTO getPaymentByReference(String paymentReference);

    /**
     * Get payment with full status history.
     */
    PaymentResponseDTO getPaymentWithHistory(Long id);

    /**
     * Get all payments with pagination.
     */
    PagedResponseDTO<PaymentResponseDTO> getAllPayments(Pageable pageable);

    /**
     * Get payments by status.
     */
    PagedResponseDTO<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status, Pageable pageable);

    /**
     * Get payments by source account.
     */
    PagedResponseDTO<PaymentResponseDTO> getPaymentsBySourceAccount(String sourceAccount, Pageable pageable);

    /**
     * Search payments by reference or description.
     */
    PagedResponseDTO<PaymentResponseDTO> searchPayments(String searchTerm, Pageable pageable);

    /**
     * Get status history for a payment.
     */
    List<PaymentStatusHistoryDTO> getPaymentHistory(Long paymentId);

    /**
     * Update payment status.
     */
    PaymentResponseDTO updatePaymentStatus(Long id, PaymentStatusUpdateDTO updateDTO);

    /**
     * Process payment through validation (simulate).
     */
    PaymentResponseDTO validatePayment(Long id);

    /**
     * Send payment (simulate).
     */
    PaymentResponseDTO sendPayment(Long id);

    /**
     * Complete payment (simulate).
     */
    PaymentResponseDTO completePayment(Long id);

    /**
     * Fail payment with error.
     */
    PaymentResponseDTO failPayment(Long id, String errorCode, String errorMessage);

    /**
     * Get count of payments by status.
     */
    long getPaymentCountByStatus(PaymentStatus status);
}
