package com.demo.MoneyMap.entity.enums;

/**
 * Enum representing the lifecycle status of a payment.
 * Payment lifecycle: CREATED → VALIDATED → SENT → COMPLETED (or FAILED at any stage)
 */
public enum PaymentStatus {
    CREATED("Created", "Payment has been submitted but not yet validated"),
    VALIDATED("Validated", "Payment has passed all validation rules and is ready to be sent"),
    SENT("Sent", "Payment has been transmitted to the destination system"),
    COMPLETED("Completed", "Payment has been successfully processed and confirmed"),
    FAILED("Failed", "Payment has failed at some point in the process");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if transition from this status to target status is valid.
     */
    public boolean canTransitionTo(PaymentStatus target) {
        if (target == FAILED) {
            // Can fail from any non-terminal state
            return this != COMPLETED && this != FAILED;
        }
        
        return switch (this) {
            case CREATED -> target == VALIDATED;
            case VALIDATED -> target == SENT;
            case SENT -> target == COMPLETED;
            case COMPLETED, FAILED -> false; // Terminal states
        };
    }
}
