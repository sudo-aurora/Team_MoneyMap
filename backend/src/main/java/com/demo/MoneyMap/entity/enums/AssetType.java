package com.demo.MoneyMap.entity.enums;

/**
 * Enum representing the types of assets that can be managed in a portfolio.
 * Supports Gold, Stocks, Mutual Funds, and Crypto Currencies.
 */
public enum AssetType {
    GOLD("Gold", "Physical gold or gold-related investments"),
    STOCK("Stock", "Equity shares in publicly traded companies"),
    MUTUAL_FUND("Mutual Fund", "Professionally managed investment funds"),
    CRYPTO("Cryptocurrency", "Digital or virtual currencies");

    private final String displayName;
    private final String description;

    AssetType(String displayName, String description) {
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
