package com.demo.MoneyMap.entity.enums;

/**
 * Enum representing the types of monitoring rules for transaction alerts.
 */
public enum RuleType {
    AMOUNT_THRESHOLD("Amount Threshold", "Trigger alert when a single transaction exceeds a threshold amount"),
    VELOCITY("Velocity", "Trigger alert when N transactions occur within T time period"),
    NEW_PAYEE("New Payee", "Trigger alert when a transaction is made to a previously unseen payee"),
    DAILY_LIMIT("Daily Limit", "Trigger alert when cumulative transaction amount exceeds daily limit");

    private final String displayName;
    private final String description;

    RuleType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
