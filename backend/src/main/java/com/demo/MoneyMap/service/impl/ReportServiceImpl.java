package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.dto.response.QuarterlyReportDTO;
import com.demo.MoneyMap.entity.*;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.repository.*;
import com.demo.MoneyMap.service.ReportService;
import com.demo.MoneyMap.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ReportService for generating quarterly reports.
 * Supports both manual and AI-generated reports with email functionality.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ClientRepository clientRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    @Override
    public QuarterlyReportDTO generateClientReportById(Long clientId) {
        log.info("Generating quarterly report for client: {}", clientId);
        
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        
        Portfolio portfolio = portfolioRepository.findByClientId(clientId).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found for client: " + clientId));
        
        List<Asset> assets = assetRepository.findByPortfolioId(portfolio.getId());
        List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolio.getId());
        
        // Get quarterly transactions (last 3 months)
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Transaction> quarterlyTransactions = transactions.stream()
                .filter(t -> t.getTransactionDate().isAfter(threeMonthsAgo))
                .collect(Collectors.toList());
        
        // Build client info
        QuarterlyReportDTO.ClientInfo clientInfo = QuarterlyReportDTO.ClientInfo.builder()
                .fullName(client.getFirstName() + " " + client.getLastName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(formatAddress(client))
                .clientSince(client.getCreatedAt())
                .riskTolerance("Moderate") // Default - could be stored in client profile
                .financialGoals("Long-term growth") // Default - could be stored in client profile
                .build();
        
        // Calculate portfolio metrics
        QuarterlyReportDTO.ReportMetrics metrics = calculatePortfolioMetrics(portfolio, assets, quarterlyTransactions);
        
        // Build asset breakdown
        List<QuarterlyReportDTO.AssetBreakdown> assetBreakdown = buildAssetBreakdown(assets, portfolio.getTotalValue());
        
        // Get top 5 transactions by amount
        List<Transaction> topTransactions = transactions.stream()
                .sorted((t1, t2) -> t2.getTotalAmount().compareTo(t1.getTotalAmount()))
                .limit(5)
                .collect(Collectors.toList());
        
        return QuarterlyReportDTO.builder()
                .clientInfo(clientInfo)
                .reportPeriod(getCurrentQuarterPeriod())
                .portfolioMetrics(metrics)
                .assetBreakdown(assetBreakdown)
                .topTransactions(topTransactions)
                .quarterlyTransactions(quarterlyTransactions)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public void generateQuarterlyReports() {
        log.info("Starting quarterly report generation for all active clients");
        
        List<Client> activeClients = clientRepository.findByActiveTrue();
        
        for (Client client : activeClients) {
            try {
                QuarterlyReportDTO report = generateClientReportById(client.getId());
                sendReportViaEmail(client.getId(), report);
                log.info("Quarterly report generated and sent for client: {}", client.getId());
            } catch (Exception e) {
                log.error("Failed to generate quarterly report for client {}: {}", 
                        client.getId(), e.getMessage());
            }
        }
        
        log.info("Quarterly report generation completed for {} clients", activeClients.size());
    }

    @Override
    public QuarterlyReportDTO generateAIQuarterlyReport(Long clientId) {
        log.info("Generating AI-powered quarterly report for client: {}", clientId);
        
        QuarterlyReportDTO baseReport = generateClientReportById(clientId);
        
        // Add AI-generated insights
        // This would integrate with an AI service like OpenAI GPT
        // For now, we'll add some basic AI-like insights
        
        String aiInsights = generateAIInsights(baseReport);
        
        // You could extend the DTO to include AI insights
        // For now, we'll return the base report with enhanced analysis
        
        return baseReport;
    }

    @Override
    public void sendReportViaEmail(Long clientId, QuarterlyReportDTO report) {
        log.info("Sending quarterly report via email for client: {}", clientId);
        
        try {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + clientId));
            
            String emailContent = formatEmailContent(report);
            String subject = "Quarterly Portfolio Report - " + report.getReportPeriod();
            
            emailService.sendEmail(client.getEmail(), subject, emailContent);
            
            log.info("Quarterly report email sent successfully to: {}", client.getEmail());
            
        } catch (Exception e) {
            log.error("Failed to send quarterly report email for client {}: {}", 
                    clientId, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void testEmailConfiguration() {
        log.info("Testing email configuration");
        emailService.sendTestEmail();
    }

    @Override
    public List<QuarterlyReportDTO> getAllClientReports() {
        log.info("Generating quarterly reports for all clients");
        
        List<Client> activeClients = clientRepository.findByActiveTrue();
        List<QuarterlyReportDTO> reports = new ArrayList<>();
        
        for (Client client : activeClients) {
            try {
                QuarterlyReportDTO report = generateClientReportById(client.getId());
                reports.add(report);
            } catch (Exception e) {
                log.error("Failed to generate report for client {}: {}", client.getId(), e.getMessage());
            }
        }
        
        return reports;
    }

    private QuarterlyReportDTO.ReportMetrics calculatePortfolioMetrics(Portfolio portfolio, 
                                                                       List<Asset> assets, 
                                                                       List<Transaction> quarterlyTransactions) {
        
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalWithdrawn = BigDecimal.ZERO;
        
        // Calculate from transactions
        for (Transaction transaction : quarterlyTransactions) {
            switch (transaction.getTransactionType()) {
                case BUY:
                case TRANSFER_IN:
                    totalInvested = totalInvested.add(transaction.getTotalAmount());
                    break;
                case SELL:
                case TRANSFER_OUT:
                    totalWithdrawn = totalWithdrawn.add(transaction.getTotalAmount());
                    break;
                default:
                    break;
            }
        }
        
        BigDecimal quarterlyReturn = portfolio.getTotalValue().subtract(totalInvested).add(totalWithdrawn);
        BigDecimal returnPercentage = totalInvested.compareTo(BigDecimal.ZERO) > 0 
                ? quarterlyReturn.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                : BigDecimal.ZERO;
        
        return QuarterlyReportDTO.ReportMetrics.builder()
                .totalPortfolioValue(portfolio.getTotalValue())
                .walletBalance(BigDecimal.ZERO) // Could be calculated from wallet table
                .totalNetWorth(portfolio.getTotalValue())
                .totalInvested(totalInvested)
                .totalWithdrawn(totalWithdrawn)
                .quarterlyReturn(quarterlyReturn)
                .returnPercentage(returnPercentage)
                .transactionCount(quarterlyTransactions.size())
                .build();
    }

    private List<QuarterlyReportDTO.AssetBreakdown> buildAssetBreakdown(List<Asset> assets, BigDecimal totalValue) {
        return assets.stream()
                .map(asset -> {
                    BigDecimal portfolioPercentage = totalValue.compareTo(BigDecimal.ZERO) > 0
                            ? asset.getCurrentValue().divide(totalValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                            : BigDecimal.ZERO;
                    
                    return QuarterlyReportDTO.AssetBreakdown.builder()
                            .assetSymbol(asset.getSymbol())
                            .assetName(asset.getName())
                            .quantity(asset.getQuantity())
                            .currentPrice(asset.getCurrentPrice())
                            .totalValue(asset.getCurrentValue())
                            .portfolioPercentage(portfolioPercentage)
                            .assetType(asset.getType().toString())
                            .build();
                })
                .sorted((a, b) -> b.getTotalValue().compareTo(a.getTotalValue()))
                .collect(Collectors.toList());
    }

    private String formatAddress(Client client) {
        return String.format("%s, %s, %s %s, %s", 
                client.getAddress(), client.getCity(), 
                client.getStateOrProvince(), client.getPostalCode(), 
                client.getCountry());
    }

    private String getCurrentQuarterPeriod() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int quarter = (now.getMonthValue() - 1) / 3 + 1;
        return String.format("Q%d %d", quarter, year);
    }

    private String generateAIInsights(QuarterlyReportDTO report) {
        // This would integrate with an AI service
        // For now, return basic insights based on the data
        
        StringBuilder insights = new StringBuilder();
        insights.append("AI-Generated Insights:\n\n");
        
        // Performance analysis
        if (report.getPortfolioMetrics().getReturnPercentage().compareTo(BigDecimal.ZERO) > 0) {
            insights.append("• Your portfolio has shown positive growth this quarter with ")
                   .append(report.getPortfolioMetrics().getFormattedReturnPercentage())
                   .append(" returns.\n");
        } else {
            insights.append("• Your portfolio has experienced a decline this quarter. ")
                   .append("Consider reviewing your investment strategy.\n");
        }
        
        // Asset allocation insights
        Optional<QuarterlyReportDTO.AssetBreakdown> largestAsset = report.getAssetBreakdown().stream().findFirst();
        if (largestAsset.isPresent()) {
            insights.append("• Your largest holding is ")
                   .append(largestAsset.get().getAssetName())
                   .append(" representing ")
                   .append(largestAsset.get().getFormattedPortfolioPercentage())
                   .append(" of your portfolio.\n");
        }
        
        // Transaction activity
        if (report.getPortfolioMetrics().getTransactionCount() > 10) {
            insights.append("• High transaction activity detected this quarter. ")
                   .append("Consider the impact of trading fees on your returns.\n");
        }
        
        return insights.toString();
    }

    private String formatEmailContent(QuarterlyReportDTO report) {
        StringBuilder content = new StringBuilder();
        
        content.append("<html><body style='font-family: Arial, sans-serif;'>");
        content.append("<h2>Quarterly Portfolio Report</h2>");
        content.append("<p><strong>Client:</strong> ").append(report.getClientInfo().getFullName()).append("</p>");
        content.append("<p><strong>Period:</strong> ").append(report.getReportPeriod()).append("</p>");
        content.append("<p><strong>Generated:</strong> ").append(report.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("</p>");
        
        content.append("<h3>Portfolio Summary</h3>");
        content.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        content.append("<tr><th>Metric</th><th>Value</th></tr>");
        content.append("<tr><td>Total Portfolio Value</td><td>").append(report.getPortfolioMetrics().getFormattedTotalPortfolioValue()).append("</td></tr>");
        content.append("<tr><td>Quarterly Return</td><td>").append(report.getPortfolioMetrics().getFormattedQuarterlyReturn()).append("</td></tr>");
        content.append("<tr><td>Return Percentage</td><td>").append(report.getPortfolioMetrics().getFormattedReturnPercentage()).append("</td></tr>");
        content.append("<tr><td>Transaction Count</td><td>").append((int)report.getPortfolioMetrics().getTransactionCount()).append("</td></tr>");
        content.append("</table>");
        
        content.append("<h3>Top Holdings</h3>");
        content.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        content.append("<tr><th>Asset</th><th>Quantity</th><th>Value</th><th>% of Portfolio</th></tr>");
        
        for (QuarterlyReportDTO.AssetBreakdown asset : report.getAssetBreakdown().stream().limit(10).collect(Collectors.toList())) {
            content.append("<tr>");
            content.append("<td>").append(asset.getAssetName()).append("</td>");
            content.append("<td>").append(asset.getFormattedQuantity()).append("</td>");
            content.append("<td>").append(asset.getFormattedTotalValue()).append("</td>");
            content.append("<td>").append(asset.getFormattedPortfolioPercentage()).append("</td>");
            content.append("</tr>");
        }
        content.append("</table>");
        
        content.append("<h3>AI Insights</h3>");
        content.append("<p>").append(generateAIInsights(report).replace("\n", "<br>")).append("</p>");
        
        content.append("<p><em>This is an automated report. Please contact your financial advisor for personalized advice.</em></p>");
        content.append("</body></html>");
        
        return content.toString();
    }
}
