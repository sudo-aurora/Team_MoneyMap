package com.demo.MoneyMap.entity.enums;

/**
 * Enum representing the severity level of an alert.
 */
public enum AlertSeverity {
    HIGH("High", "Critical alert requiring immediate attention", 1),
    MEDIUM("Medium", "Important alert that should be reviewed soon", 2),
    LOW("Low", "Informational alert for routine review", 3);

    private final String displayName;
    private final String description;
    private final int priority;

    AlertSeverity(String displayName, String description, int priority) {
        this.displayName = displayName;
        this.description = description;
        this.priority = priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
