package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.request.TransactionRequestDTO;
import com.demo.MoneyMap.dto.response.TransactionResponseDTO;
import com.demo.MoneyMap.dto.response.AvailableAssetDTO;
import com.demo.MoneyMap.entity.Asset;
import com.demo.MoneyMap.entity.Client;
import com.demo.MoneyMap.entity.Portfolio;
import com.demo.MoneyMap.entity.enums.AssetType;
import com.demo.MoneyMap.entity.enums.AvailableAsset;
import com.demo.MoneyMap.entity.enums.TransactionType;
import com.demo.MoneyMap.exception.InsufficientFundsException;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.exception.BadRequestException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for handling asset trading operations (buy/sell).
 * Integrates with client wallet and transaction system.
 */
public interface AssetTradingService {
    
    /**
     * Buy an asset for a client
     */
    TransactionResponseDTO buyAsset(Long clientId, String symbol, BigDecimal quantity, BigDecimal price);
    
    /**
     * Sell an asset for a client
     */
    TransactionResponseDTO sellAsset(Long clientId, Long assetId, BigDecimal quantity, BigDecimal price);
    
    /**
     * Get available assets for trading
     */
    List<AvailableAssetDTO> getAvailableAssets();
    
    /**
     * Get available assets by type
     */
    List<AvailableAssetDTO> getAvailableAssetsByType(AssetType assetType);
    
    /**
     * Search available assets
     */
    List<AvailableAssetDTO> searchAvailableAssets(String query);
    
    /**
     * Get client wallet balance
     */
    BigDecimal getClientWalletBalance(Long clientId);
    
    /**
     * Add funds to client wallet
     */
    void addToWallet(Long clientId, BigDecimal amount);
    
    /**
     * Validate if client has sufficient funds
     */
    boolean hasSufficientFunds(Long clientId, BigDecimal amount);
}
