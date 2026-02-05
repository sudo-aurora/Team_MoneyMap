package com.demo.MoneyMap.dto.response;

import com.demo.MoneyMap.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Comprehensive quarterly report DTO containing all client portfolio information.
 * Maximizes use of available data fields for professional reporting.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuarterlyReportDTO {
    
    private ClientInfo clientInfo;
    private String reportPeriod;
    private ReportMetrics portfolioMetrics;
    private List<AssetBreakdown> assetBreakdown;
    private List<Transaction> topTransactions;
    private List<Transaction> quarterlyTransactions;
    private LocalDateTime generatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientInfo {
        private String fullName;
        private String email;
        private String phone;
        private String address;
        private LocalDateTime clientSince;
        private String riskTolerance;
        private String financialGoals;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportMetrics {
        private java.math.BigDecimal totalPortfolioValue;
        private java.math.BigDecimal walletBalance;
        private java.math.BigDecimal totalNetWorth;
        private java.math.BigDecimal totalInvested;
        private java.math.BigDecimal totalWithdrawn;
        private java.math.BigDecimal quarterlyReturn;
        private java.math.BigDecimal returnPercentage;
        private double transactionCount;
        
        public String getFormattedTotalPortfolioValue() {
            return String.format("$%,.2f", totalPortfolioValue);
        }
        
        public String getFormattedWalletBalance() {
            return String.format("$%,.2f", walletBalance);
        }
        
        public String getFormattedTotalNetWorth() {
            return String.format("$%,.2f", totalNetWorth);
        }
        
        public String getFormattedQuarterlyReturn() {
            return String.format("$%,.2f", quarterlyReturn);
        }
        
        public String getFormattedReturnPercentage() {
            return String.format("%.2f%%", returnPercentage);
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssetBreakdown {
        private String assetSymbol;
        private String assetName;
        private java.math.BigDecimal quantity;
        private java.math.BigDecimal currentPrice;
        private java.math.BigDecimal totalValue;
        private java.math.BigDecimal portfolioPercentage;
        private String assetType;
        
        public String getFormattedTotalValue() {
            return String.format("$%,.2f", totalValue);
        }
        
        public String getFormattedCurrentPrice() {
            return String.format("$%,.2f", currentPrice);
        }
        
        public String getFormattedPortfolioPercentage() {
            return String.format("%.2f%%", portfolioPercentage);
        }
        
        public String getFormattedQuantity() {
            return String.format("%,.2f", quantity);
        }
    }
}
