package com.demo.MoneyMap.entity.enums;

/**
 * Enum representing the types of transactions that can occur in a portfolio.
 */
public enum TransactionType {
    BUY("Buy", "Purchase of an asset"),
    SELL("Sell", "Sale of an asset"),
    DIVIDEND("Dividend", "Dividend received from an asset"),
    INTEREST("Interest", "Interest earned on an asset"),
    TRANSFER_IN("Transfer In", "Asset transferred into portfolio"),
    TRANSFER_OUT("Transfer Out", "Asset transferred out of portfolio");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
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
