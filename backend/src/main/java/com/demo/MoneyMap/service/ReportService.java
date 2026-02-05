package com.demo.MoneyMap.service;

import com.demo.MoneyMap.dto.response.QuarterlyReportDTO;
import java.util.List;

/**
 * Service interface for generating client reports.
 * Provides quarterly report generation with AI and manual options.
 */
public interface ReportService {
    
    /**
     * Generate a comprehensive quarterly report for a specific client.
     * @param clientId The client ID to generate report for
     * @return Complete quarterly report with all portfolio metrics
     */
    QuarterlyReportDTO generateClientReportById(Long clientId);
    
    /**
     * Generate quarterly reports for all active clients.
     * Used for scheduled quarterly report generation.
     */
    void generateQuarterlyReports();
    
    /**
     * Generate AI-powered quarterly report for a specific client.
     * Uses AI to generate insights and recommendations.
     * @param clientId The client ID to generate AI report for
     * @return AI-enhanced quarterly report
     */
    QuarterlyReportDTO generateAIQuarterlyReport(Long clientId);
    
    /**
     * Send quarterly report via email to client.
     * @param clientId The client ID to send report to
     * @param report The report to send
     */
    void sendReportViaEmail(Long clientId, QuarterlyReportDTO report);
    
    /**
     * Test email configuration.
     * Sends a test email to verify email settings.
     */
    void testEmailConfiguration();
    
    /**
     * Get quarterly report summary for all clients.
     * @return List of client report summaries
     */
    List<QuarterlyReportDTO> getAllClientReports();
}
