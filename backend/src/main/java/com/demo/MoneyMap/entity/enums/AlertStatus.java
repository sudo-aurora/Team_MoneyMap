package com.demo.MoneyMap.entity.enums;

/**
 * Enum representing the lifecycle status of an alert.
 * Alert lifecycle: OPEN → ACKNOWLEDGED → INVESTIGATING → CLOSED (or DISMISSED)
 */
public enum AlertStatus {
    OPEN("Open", "Alert has been generated but not yet reviewed"),
    ACKNOWLEDGED("Acknowledged", "Alert has been seen by an operator but not yet investigated"),
    INVESTIGATING("Investigating", "Alert is actively being investigated"),
    CLOSED("Closed", "Investigation complete, issue resolved or confirmed legitimate"),
    DISMISSED("Dismissed", "Alert determined to be false positive or not requiring action");

    private final String displayName;
    private final String description;

    AlertStatus(String displayName, String description) {
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
    public boolean canTransitionTo(AlertStatus target) {
        return switch (this) {
            case OPEN -> target == ACKNOWLEDGED || target == DISMISSED;
            case ACKNOWLEDGED -> target == INVESTIGATING || target == DISMISSED;
            case INVESTIGATING -> target == CLOSED || target == DISMISSED;
            case CLOSED, DISMISSED -> false; // Terminal states
        };
    }
}
