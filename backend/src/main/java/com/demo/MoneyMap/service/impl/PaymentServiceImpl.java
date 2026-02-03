package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.dto.request.PaymentRequestDTO;
import com.demo.MoneyMap.dto.request.PaymentStatusUpdateDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.dto.response.PaymentResponseDTO;
import com.demo.MoneyMap.dto.response.PaymentStatusHistoryDTO;
import com.demo.MoneyMap.entity.Payment;
import com.demo.MoneyMap.entity.PaymentStatusHistory;
import com.demo.MoneyMap.entity.enums.PaymentErrorCode;
import com.demo.MoneyMap.entity.enums.PaymentStatus;
import com.demo.MoneyMap.exception.BadRequestException;
import com.demo.MoneyMap.exception.DuplicateResourceException;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.mapper.PaymentMapper;
import com.demo.MoneyMap.repository.PaymentRepository;
import com.demo.MoneyMap.repository.PaymentStatusHistoryRepository;
import com.demo.MoneyMap.service.PaymentService;
import com.demo.MoneyMap.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of PaymentService.
 * Handles payment lifecycle management with full audit trail.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryRepository statusHistoryRepository;
    private final PaymentMapper paymentMapper;
    private final RuleEngineService ruleEngineService;

    private static final List<String> SUPPORTED_CURRENCIES = Arrays.asList("USD", "EUR", "GBP", "JPY", "INR", "CAD", "AUD");

    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO) {
        log.info("Creating new payment from {} to {}", requestDTO.getSourceAccount(), requestDTO.getDestinationAccount());

        // Check for duplicate using idempotency key
        if (requestDTO.getIdempotencyKey() != null && paymentRepository.existsByIdempotencyKey(requestDTO.getIdempotencyKey())) {
            Payment existing = paymentRepository.findByIdempotencyKey(requestDTO.getIdempotencyKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
            log.info("Duplicate payment request with idempotency key: {}", requestDTO.getIdempotencyKey());
            return paymentMapper.toResponseDTO(existing);
        }

        // Validate payment
        validatePaymentRequest(requestDTO);

        Payment payment = paymentMapper.toEntity(requestDTO);
        Payment savedPayment = paymentRepository.save(payment);

        // Add initial status history
        addStatusHistory(savedPayment, null, PaymentStatus.CREATED, "Payment created");

        log.info("Successfully created payment with ID: {}", savedPayment.getId());

        // Evaluate monitoring rules asynchronously
        try {
            ruleEngineService.evaluatePayment(savedPayment);
        } catch (Exception e) {
            log.warn("Rule evaluation failed for payment {}: {}", savedPayment.getId(), e.getMessage());
        }

        return paymentMapper.toResponseDTO(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = findPaymentById(id);
        return paymentMapper.toResponseDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByReference(String paymentReference) {
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with reference: " + paymentReference));
        return paymentMapper.toResponseDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentWithHistory(Long id) {
        Payment payment = paymentRepository.findByIdWithHistory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return paymentMapper.toResponseDTOWithHistory(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<PaymentResponseDTO> getAllPayments(Pageable pageable) {
        Page<Payment> paymentPage = paymentRepository.findAll(pageable);
        return mapToPagedResponse(paymentPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        Page<Payment> paymentPage = paymentRepository.findByStatus(status, pageable);
        return mapToPagedResponse(paymentPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<PaymentResponseDTO> getPaymentsBySourceAccount(String sourceAccount, Pageable pageable) {
        Page<Payment> paymentPage = paymentRepository.findBySourceAccount(sourceAccount, pageable);
        return mapToPagedResponse(paymentPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<PaymentResponseDTO> searchPayments(String searchTerm, Pageable pageable) {
        Page<Payment> paymentPage = paymentRepository.searchPayments(searchTerm, pageable);
        return mapToPagedResponse(paymentPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentStatusHistoryDTO> getPaymentHistory(Long paymentId) {
        // Verify payment exists
        findPaymentById(paymentId);
        
        return statusHistoryRepository.findByPaymentIdOrderByTimestampDesc(paymentId).stream()
                .map(paymentMapper::toHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponseDTO updatePaymentStatus(Long id, PaymentStatusUpdateDTO updateDTO) {
        log.info("Updating payment {} status to {}", id, updateDTO.getStatus());

        Payment payment = findPaymentById(id);
        PaymentStatus currentStatus = payment.getStatus();
        PaymentStatus newStatus = updateDTO.getStatus();

        // Validate status transition
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new BadRequestException(String.format(
                    "Invalid status transition from %s to %s", currentStatus, newStatus));
        }

        // If failing, set error details
        if (newStatus == PaymentStatus.FAILED) {
            payment.setErrorCode(updateDTO.getErrorCode());
            payment.setErrorMessage(updateDTO.getErrorMessage());
        }

        payment.setStatus(newStatus);
        Payment updatedPayment = paymentRepository.save(payment);

        // Add status history
        addStatusHistory(updatedPayment, currentStatus, newStatus, updateDTO.getNotes());

        log.info("Successfully updated payment {} status to {}", id, newStatus);
        return paymentMapper.toResponseDTO(updatedPayment);
    }

    @Override
    public PaymentResponseDTO validatePayment(Long id) {
        log.info("Validating payment {}", id);
        PaymentStatusUpdateDTO updateDTO = PaymentStatusUpdateDTO.builder()
                .status(PaymentStatus.VALIDATED)
                .notes("All validation checks passed")
                .build();
        return updatePaymentStatus(id, updateDTO);
    }

    @Override
    public PaymentResponseDTO sendPayment(Long id) {
        log.info("Sending payment {}", id);
        PaymentStatusUpdateDTO updateDTO = PaymentStatusUpdateDTO.builder()
                .status(PaymentStatus.SENT)
                .notes("Payment transmitted to destination")
                .build();
        return updatePaymentStatus(id, updateDTO);
    }

    @Override
    public PaymentResponseDTO completePayment(Long id) {
        log.info("Completing payment {}", id);
        PaymentStatusUpdateDTO updateDTO = PaymentStatusUpdateDTO.builder()
                .status(PaymentStatus.COMPLETED)
                .notes("Payment successfully processed")
                .build();
        return updatePaymentStatus(id, updateDTO);
    }

    @Override
    public PaymentResponseDTO failPayment(Long id, String errorCode, String errorMessage) {
        log.info("Failing payment {} with error {}", id, errorCode);
        PaymentErrorCode code = PaymentErrorCode.valueOf(errorCode);
        PaymentStatusUpdateDTO updateDTO = PaymentStatusUpdateDTO.builder()
                .status(PaymentStatus.FAILED)
                .errorCode(code)
                .errorMessage(errorMessage)
                .notes("Payment failed: " + errorMessage)
                .build();
        return updatePaymentStatus(id, updateDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public long getPaymentCountByStatus(PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }

    /**
     * Validate payment request.
     */
    private void validatePaymentRequest(PaymentRequestDTO requestDTO) {
        // Check same account
        if (requestDTO.getSourceAccount().equals(requestDTO.getDestinationAccount())) {
            throw new BadRequestException("Source and destination accounts cannot be the same");
        }

        // Check currency is supported
        if (!SUPPORTED_CURRENCIES.contains(requestDTO.getCurrency().toUpperCase())) {
            throw new BadRequestException("Currency " + requestDTO.getCurrency() + " is not supported. Supported currencies: " + SUPPORTED_CURRENCIES);
        }
    }

    /**
     * Add status history entry.
     */
    private void addStatusHistory(Payment payment, PaymentStatus previousStatus, PaymentStatus newStatus, String notes) {
        PaymentStatusHistory history = PaymentStatusHistory.builder()
                .payment(payment)
                .previousStatus(previousStatus)
                .status(newStatus)
                .notes(notes)
                .build();
        statusHistoryRepository.save(history);
    }

    /**
     * Find payment by ID or throw exception.
     */
    private Payment findPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
    }

    /**
     * Map Page to PagedResponseDTO.
     */
    private PagedResponseDTO<PaymentResponseDTO> mapToPagedResponse(Page<Payment> page) {
        List<PaymentResponseDTO> content = page.getContent().stream()
                .map(paymentMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PagedResponseDTO.<PaymentResponseDTO>builder()
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
