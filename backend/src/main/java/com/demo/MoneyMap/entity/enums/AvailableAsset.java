package com.demo.MoneyMap.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Hardcoded list of available assets for trading.
 * This serves as our market catalog of tradable assets.
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AvailableAsset {
    
    // STOCKS
    AAPL("AAPL", "Apple Inc.", AssetType.STOCK, new BigDecimal("175.50"), "NASDAQ"),
    GOOGL("GOOGL", "Alphabet Inc.", AssetType.STOCK, new BigDecimal("140.25"), "NASDAQ"),
    MSFT("MSFT", "Microsoft Corporation", AssetType.STOCK, new BigDecimal("380.75"), "NASDAQ"),
    TSLA("TSLA", "Tesla Inc.", AssetType.STOCK, new BigDecimal("245.80"), "NASDAQ"),
    AMZN("AMZN", "Amazon.com Inc.", AssetType.STOCK, new BigDecimal("155.30"), "NASDAQ"),
    META("META", "Meta Platforms Inc.", AssetType.STOCK, new BigDecimal("485.20"), "NASDAQ"),
    NVDA("NVDA", "NVIDIA Corporation", AssetType.STOCK, new BigDecimal("875.40"), "NASDAQ"),
    JPM("JPM", "JPMorgan Chase & Co.", AssetType.STOCK, new BigDecimal("195.60"), "NYSE"),
    V("V", "Visa Inc.", AssetType.STOCK, new BigDecimal("275.80"), "NYSE"),
    JNJ("JNJ", "Johnson & Johnson", AssetType.STOCK, new BigDecimal("165.40"), "NYSE"),
    
    // CRYPTOCURRENCIES
    BTC("BTC", "Bitcoin", AssetType.CRYPTO, new BigDecimal("43250.00"), "Bitcoin"),
    ETH("ETH", "Ethereum", AssetType.CRYPTO, new BigDecimal("2280.50"), "Ethereum"),
    ADA("ADA", "Cardano", AssetType.CRYPTO, new BigDecimal("0.58"), "Cardano"),
    SOL("SOL", "Solana", AssetType.CRYPTO, new BigDecimal("98.75"), "Solana"),
    DOT("DOT", "Polkadot", AssetType.CRYPTO, new BigDecimal("7.85"), "Polkadot"),
    MATIC("MATIC", "Polygon", AssetType.CRYPTO, new BigDecimal("0.92"), "Polygon"),
    LINK("LINK", "Chainlink", AssetType.CRYPTO, new BigDecimal("14.65"), "Ethereum"),
    UNI("UNI", "Uniswap", AssetType.CRYPTO, new BigDecimal("6.25"), "Ethereum"),
    ATOM("ATOM", "Cosmos", AssetType.CRYPTO, new BigDecimal("12.45"), "Cosmos"),
    AVAX("AVAX", "Avalanche", AssetType.CRYPTO, new BigDecimal("38.90"), "Avalanche"),
    
    // GOLD
    GOLD_24K("GOLD24K", "24 Karat Gold", AssetType.GOLD, new BigDecimal("68.50"), "24K"),
    GOLD_22K("GOLD22K", "22 Karat Gold", AssetType.GOLD, new BigDecimal("62.75"), "22K"),
    GOLD_18K("GOLD18K", "18 Karat Gold", AssetType.GOLD, new BigDecimal("51.25"), "18K"),
    SILVER("SILVER", "Silver", AssetType.GOLD, new BigDecimal("0.95"), "Silver"),
    
    // MUTUAL FUNDS
    VFIAX("VFIAX", "Vanguard 500 Index Admiral", AssetType.MUTUAL_FUND, new BigDecimal("425.30"), "Vanguard"),
    FXAIX("FXAIX", "Fidelity 500 Index", AssetType.MUTUAL_FUND, new BigDecimal("118.75"), "Fidelity"),
    SWPPX("SWPPX", "Schwab S&P 500 Index", AssetType.MUTUAL_FUND, new BigDecimal("95.40"), "Charles Schwab"),
    VTSAX("VTSAX", "Vanguard Total Stock Market Admiral", AssetType.MUTUAL_FUND, new BigDecimal("245.80"), "Vanguard"),
    FSKAX("FSKAX", "Fidelity Total Market Index", AssetType.MUTUAL_FUND, new BigDecimal("135.60"), "Fidelity");
    
    private final String symbol;
    private final String name;
    private final AssetType assetType;
    private final BigDecimal currentMarketPrice;
    private final String exchangeOrNetwork;
    
    AvailableAsset(String symbol, String name, AssetType assetType, BigDecimal currentMarketPrice, String exchangeOrNetwork) {
        this.symbol = symbol;
        this.name = name;
        this.assetType = assetType;
        this.currentMarketPrice = currentMarketPrice;
        this.exchangeOrNetwork = exchangeOrNetwork;
    }
    
    /**
     * Find asset by symbol (case insensitive)
     */
    public static Optional<AvailableAsset> findBySymbol(String symbol) {
        return Arrays.stream(values())
                .filter(asset -> asset.symbol.equalsIgnoreCase(symbol))
                .findFirst();
    }
    
    /**
     * Get all assets by type
     */
    public static List<AvailableAsset> findByType(AssetType assetType) {
        return Arrays.stream(values())
                .filter(asset -> asset.assetType == assetType)
                .toList();
    }
    
    /**
     * Get all available asset types
     */
    public static List<AssetType> getAvailableTypes() {
        return Arrays.stream(values())
                .map(AvailableAsset::getAssetType)
                .distinct()
                .toList();
    }
    
    /**
     * Search assets by name or symbol
     */
    public static List<AvailableAsset> searchAssets(String query) {
        String lowerQuery = query.toLowerCase();
        return Arrays.stream(values())
                .filter(asset -> 
                    asset.symbol.toLowerCase().contains(lowerQuery) ||
                    asset.name.toLowerCase().contains(lowerQuery))
                .toList();
    }
    
    /**
     * Check if symbol exists
     */
    public static boolean symbolExists(String symbol) {
        return findBySymbol(symbol).isPresent();
    }
}
