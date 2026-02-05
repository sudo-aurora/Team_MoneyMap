package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.config.AssetFactory;
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
import com.demo.MoneyMap.repository.AssetRepository;
import com.demo.MoneyMap.repository.ClientRepository;
import com.demo.MoneyMap.repository.PortfolioRepository;
import com.demo.MoneyMap.service.AssetTradingService;
import com.demo.MoneyMap.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of AssetTradingService.
 * Handles buy/sell operations with wallet integration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssetTradingServiceImpl implements AssetTradingService {

    private final ClientRepository clientRepository;
    private final AssetRepository assetRepository;
    private final PortfolioRepository portfolioRepository;
    private final TransactionService transactionService;
    private final AssetFactory assetFactory;

    @Override
    public TransactionResponseDTO buyAsset(Long clientId, String symbol, BigDecimal quantity, BigDecimal price) {
        log.info("Processing buy order: {} {} for client ID: {}", quantity, symbol, clientId);

        // Validate symbol exists in available assets
        AvailableAsset availableAsset = AvailableAsset.findBySymbol(symbol)
                .orElseThrow(() -> new BadRequestException("Asset symbol '" + symbol + "' is not available for trading"));

        // Get client with pessimistic locking to prevent concurrent modifications
        Client client = clientRepository.findByIdWithLock(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));

        // Calculate total cost
        BigDecimal totalCost = quantity.multiply(price);

        // Check wallet balance
        if (!client.hasSufficientFunds(totalCost)) {
            throw new InsufficientFundsException(
                    String.format("Insufficient wallet balance. Required: $%.2f, Available: $%.2f", 
                            totalCost, client.getWalletBalance()));
        }

        // Get or create client's portfolio (assuming one portfolio per client for simplicity)
        Portfolio portfolio = portfolioRepository.findByClientId(clientId)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Portfolio newPortfolio = Portfolio.builder()
                            .name("Primary Portfolio")
                            .client(client)
                            .totalValue(BigDecimal.ZERO)
                            .build();
                    return portfolioRepository.save(newPortfolio);
                });

        // Find existing asset or create new one
        Asset asset = findOrCreateAsset(portfolio, availableAsset, quantity, price);

        // Deduct from wallet
        client.deductFromWallet(totalCost);
        clientRepository.save(client);

        // Create BUY transaction
        TransactionRequestDTO transactionRequest = TransactionRequestDTO.builder()
                .assetId(asset.getId())
                .transactionType(TransactionType.BUY)
                .quantity(quantity)
                .pricePerUnit(price)
                .transactionDate(LocalDateTime.now())
                .notes(String.format("Purchased %s shares of %s at $%.2f", quantity, symbol, price))
                .build();

        TransactionResponseDTO transaction = transactionService.createTransaction(transactionRequest);

        log.info("Successfully processed buy order: {} {} for client ID: {}", quantity, symbol, clientId);
        return transaction;
    }

    @Override
    public TransactionResponseDTO sellAsset(Long clientId, Long assetId, BigDecimal quantity, BigDecimal price) {
        log.info("Processing sell order: {} of asset ID: {} for client ID: {}", quantity, assetId, clientId);

        // Get asset with portfolio and client validation
        Asset asset = assetRepository.findByIdWithPortfolio(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + assetId));

        // Validate asset belongs to the client
        if (!asset.getPortfolio().getClient().getId().equals(clientId)) {
            throw new BadRequestException("Asset does not belong to the specified client");
        }

        // Check if client has sufficient quantity
        if (asset.getQuantity().compareTo(quantity) < 0) {
            throw new BadRequestException(
                    String.format("Insufficient asset quantity. Available: %s, Requested: %s", 
                            asset.getQuantity(), quantity));
        }

        // Get client with locking
        Client client = clientRepository.findByIdWithLock(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));

        // Calculate total proceeds
        BigDecimal totalProceeds = quantity.multiply(price);

        // Add to wallet
        client.addToWallet(totalProceeds);
        clientRepository.save(client);

        // Create SELL transaction
        TransactionRequestDTO transactionRequest = TransactionRequestDTO.builder()
                .assetId(assetId)
                .transactionType(TransactionType.SELL)
                .quantity(quantity)
                .pricePerUnit(price)
                .transactionDate(LocalDateTime.now())
                .notes(String.format("Sold %s shares of %s at $%.2f", quantity, asset.getSymbol(), price))
                .build();

        TransactionResponseDTO transaction = transactionService.createTransaction(transactionRequest);

        // If asset quantity becomes zero, consider deleting the asset
        if (asset.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            log.info("Asset quantity is zero, keeping asset record for history");
        }

        log.info("Successfully processed sell order: {} of asset ID: {} for client ID: {}", quantity, assetId, clientId);
        return transaction;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableAssetDTO> getAvailableAssets() {
        return Arrays.stream(AvailableAsset.values())
                .map(asset -> AvailableAssetDTO.builder()
                        .symbol(asset.getSymbol())
                        .name(asset.getName())
                        .assetType(asset.getAssetType().name())
                        .currentMarketPrice(asset.getCurrentMarketPrice())
                        .exchangeOrNetwork(asset.getExchangeOrNetwork())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableAssetDTO> getAvailableAssetsByType(AssetType assetType) {
        return AvailableAsset.findByType(assetType)
                .stream()
                .map(asset -> AvailableAssetDTO.builder()
                        .symbol(asset.getSymbol())
                        .name(asset.getName())
                        .assetType(asset.getAssetType().name())
                        .currentMarketPrice(asset.getCurrentMarketPrice())
                        .exchangeOrNetwork(asset.getExchangeOrNetwork())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableAssetDTO> searchAvailableAssets(String query) {
        return AvailableAsset.searchAssets(query)
                .stream()
                .map(asset -> AvailableAssetDTO.builder()
                        .symbol(asset.getSymbol())
                        .name(asset.getName())
                        .assetType(asset.getAssetType().name())
                        .currentMarketPrice(asset.getCurrentMarketPrice())
                        .exchangeOrNetwork(asset.getExchangeOrNetwork())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getClientWalletBalance(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));
        return client.getWalletBalance();
    }

    @Override
    public void addToWallet(Long clientId, BigDecimal amount) {
        log.info("Adding $%.2f to wallet for client ID: {}", amount, clientId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be positive");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));

        client.addToWallet(amount);
        if (client.getWalletCreatedAt() == null) {
            client.setWalletCreatedAt(LocalDateTime.now());
        }

        clientRepository.save(client);

        log.info("Successfully added $%.2f to wallet for client ID: {}. New balance: $%.2f", 
                amount, clientId, client.getWalletBalance());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSufficientFunds(Long clientId, BigDecimal amount) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));
        return client.hasSufficientFunds(amount);
    }

    /**
     * Find existing asset in portfolio or create new one
     */
    private Asset findOrCreateAsset(Portfolio portfolio, AvailableAsset availableAsset, BigDecimal quantity, BigDecimal price) {
        // Try to find existing asset with same symbol in portfolio
        List<Asset> existingAssets = assetRepository.findByPortfolioId(portfolio.getId());
        
        for (Asset existingAsset : existingAssets) {
            if (existingAsset.getSymbol().equalsIgnoreCase(availableAsset.getSymbol())) {
                log.info("Found existing asset {} in portfolio, will add to quantity", availableAsset.getSymbol());
                return existingAsset;
            }
        }

        // Create new asset
        log.info("Creating new asset {} in portfolio", availableAsset.getSymbol());
        
        Asset newAsset = assetFactory.createFromDTO(com.demo.MoneyMap.dto.request.AssetRequestDTO.builder()
                .name(availableAsset.getName())
                .symbol(availableAsset.getSymbol())
                .assetType(availableAsset.getAssetType())
                .quantity(BigDecimal.ZERO) // Will be updated by transaction
                .purchasePrice(price)
                .currentPrice(price)
                .purchaseDate(LocalDateTime.now().toLocalDate())
                .portfolioId(portfolio.getId())
                .build());

        newAsset.setPortfolio(portfolio);
        return assetRepository.save(newAsset);
    }
}
