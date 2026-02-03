package com.demo.MoneyMap.config;

import com.demo.MoneyMap.entity.Transaction;
import com.demo.MoneyMap.entity.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Factory class for creating Transaction objects.
 * Implements the Factory Pattern for consistent transaction creation.
 * 
 * This follows the Gang of Four (GoF) Factory Pattern:
 * - Encapsulates transaction creation logic
 * - Provides specialized methods for each transaction type
 * - Ensures consistent transaction initialization
 */
@Component
public class TransactionFactory {

    /**
     * Create a BUY transaction.
     */
    public Transaction createBuyTransaction(BigDecimal quantity, BigDecimal pricePerUnit, 
                                            BigDecimal fees, LocalDateTime transactionDate, String notes) {
        return createTransaction(TransactionType.BUY, quantity, pricePerUnit, fees, transactionDate, notes);
    }

    /**
     * Create a SELL transaction.
     */
    public Transaction createSellTransaction(BigDecimal quantity, BigDecimal pricePerUnit, 
                                             BigDecimal fees, LocalDateTime transactionDate, String notes) {
        return createTransaction(TransactionType.SELL, quantity, pricePerUnit, fees, transactionDate, notes);
    }

    /**
     * Create a DIVIDEND transaction.
     */
    public Transaction createDividendTransaction(BigDecimal amount, LocalDateTime transactionDate, String notes) {
        return createTransaction(TransactionType.DIVIDEND, BigDecimal.ONE, amount, BigDecimal.ZERO, transactionDate, notes);
    }

    /**
     * Create an INTEREST transaction.
     */
    public Transaction createInterestTransaction(BigDecimal amount, LocalDateTime transactionDate, String notes) {
        return createTransaction(TransactionType.INTEREST, BigDecimal.ONE, amount, BigDecimal.ZERO, transactionDate, notes);
    }

    /**
     * Create a TRANSFER_IN transaction.
     */
    public Transaction createTransferInTransaction(BigDecimal quantity, BigDecimal pricePerUnit, 
                                                   LocalDateTime transactionDate, String notes) {
        return createTransaction(TransactionType.TRANSFER_IN, quantity, pricePerUnit, BigDecimal.ZERO, transactionDate, notes);
    }

    /**
     * Create a TRANSFER_OUT transaction.
     */
    public Transaction createTransferOutTransaction(BigDecimal quantity, BigDecimal pricePerUnit, 
                                                    LocalDateTime transactionDate, String notes) {
        return createTransaction(TransactionType.TRANSFER_OUT, quantity, pricePerUnit, BigDecimal.ZERO, transactionDate, notes);
    }

    /**
     * Create a transaction of a specified type.
     * Generic factory method that can create any transaction type.
     */
    public Transaction createTransaction(TransactionType transactionType, BigDecimal quantity, 
                                         BigDecimal pricePerUnit, BigDecimal fees, 
                                         LocalDateTime transactionDate, String notes) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionType)
                .quantity(quantity)
                .pricePerUnit(pricePerUnit)
                .fees(fees != null ? fees : BigDecimal.ZERO)
                .transactionDate(transactionDate != null ? transactionDate : LocalDateTime.now())
                .notes(notes)
                .build();
        
        // Calculate total amount
        transaction.calculateTotalAmount();
        
        return transaction;
    }
}
