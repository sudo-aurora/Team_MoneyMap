package com.demo.MoneyMap.entity.enums;

/**
 * Enum representing error codes for payment failures.
 */
public enum PaymentErrorCode {
    VALIDATION_FAILED("Payment failed validation checks", 400),
    INSUFFICIENT_FUNDS("Source account has insufficient funds", 400),
    INVALID_ACCOUNT("Account number is invalid or doesn't exist", 400),
    INVALID_CURRENCY("Currency code is not supported", 400),
    INVALID_AMOUNT("Amount is zero, negative, or invalid", 400),
    DUPLICATE_PAYMENT("Payment with same idempotency key exists", 409),
    INVALID_STATUS_TRANSITION("Cannot transition from current status to requested status", 400),
    PAYMENT_NOT_FOUND("Payment ID does not exist", 404),
    PROCESSING_ERROR("Internal error during payment processing", 500),
    NETWORK_ERROR("Communication failure with payment network", 503),
    SAME_ACCOUNT("Source and destination accounts cannot be the same", 400),
    AMOUNT_EXCEEDS_LIMIT("Amount exceeds maximum allowed limit", 400);

    private final String description;
    private final int httpStatus;

    PaymentErrorCode(String description, int httpStatus) {
        this.description = description;
        this.httpStatus = httpStatus;
    }

    public String getDescription() {
        return description;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
