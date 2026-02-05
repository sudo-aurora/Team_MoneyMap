package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.dto.response.ApiResponseDTO;
import com.demo.MoneyMap.dto.response.QuarterlyReportDTO;
import com.demo.MoneyMap.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for generating and managing client reports.
 * Provides endpoints for quarterly reports and manual report generation.
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ReportController {

    private final ReportService reportService;

    /**
     * Generate quarterly report for a specific client.
     * Used for manual report generation or testing.
     */
    @GetMapping("/quarterly/{clientId}")
    @Operation(
            summary = "Generate quarterly report for client",
            description = "Generate a comprehensive quarterly report for a specific client including portfolio metrics, asset breakdown, and transaction history."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quarterly report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDTO<QuarterlyReportDTO>> generateQuarterlyReport(
            @PathVariable Long clientId) {

        log.info("Manual quarterly report generation requested for client: {}", clientId);

        try {
            // Get client and generate report
            QuarterlyReportDTO report = reportService.generateClientReportById(clientId);

            return ResponseEntity.ok(ApiResponseDTO.success(report,
                "Quarterly report generated successfully"));

        } catch (Exception e) {
            log.error("Failed to generate quarterly report for client {}: {}",
                clientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to generate quarterly report: " + e.getMessage()));
        }
    }

    /**
     * Generate AI-powered quarterly report for a specific client.
     * Uses AI to generate insights and recommendations.
     */
    @GetMapping("/quarterly/{clientId}/ai")
    @Operation(
            summary = "Generate AI-powered quarterly report for client",
            description = "Generate an AI-enhanced quarterly report with insights and recommendations."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "AI quarterly report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDTO<QuarterlyReportDTO>> generateAIQuarterlyReport(
            @PathVariable Long clientId) {

        log.info("AI quarterly report generation requested for client: {}", clientId);

        try {
            QuarterlyReportDTO report = reportService.generateAIQuarterlyReport(clientId);

            return ResponseEntity.ok(ApiResponseDTO.success(report,
                "AI quarterly report generated successfully"));

        } catch (Exception e) {
            log.error("Failed to generate AI quarterly report for client {}: {}",
                clientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to generate AI quarterly report: " + e.getMessage()));
        }
    }

    /**
     * Trigger quarterly reports for all active clients.
     * Used to manually trigger the scheduled quarterly report generation.
     */
    @PostMapping("/quarterly/generate-all")
    @Operation(
            summary = "Generate quarterly reports for all clients",
            description = "Manually trigger the quarterly report generation process for all active clients."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quarterly reports generation started"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDTO<String>> generateQuarterlyReportsForAll() {

        log.info("Manual quarterly report generation triggered for all clients");

        try {
            reportService.generateQuarterlyReports();

            return ResponseEntity.ok(ApiResponseDTO.success(
                "Quarterly report generation started for all active clients",
                "Reports will be sent via email"));

        } catch (Exception e) {
            log.error("Failed to start quarterly report generation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to start quarterly report generation: " + e.getMessage()));
        }
    }

    /**
     * Get all client quarterly reports.
     * Returns reports for all active clients.
     */
    @GetMapping("/quarterly/all")
    @Operation(
            summary = "Get all client quarterly reports",
            description = "Generate and return quarterly reports for all active clients."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All reports generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDTO<java.util.List<QuarterlyReportDTO>>> getAllQuarterlyReports() {

        log.info("Generating quarterly reports for all clients");

        try {
            java.util.List<QuarterlyReportDTO> reports = reportService.getAllClientReports();

            return ResponseEntity.ok(ApiResponseDTO.success(reports,
                "All quarterly reports generated successfully"));

        } catch (Exception e) {
            log.error("Failed to generate all quarterly reports: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to generate all quarterly reports: " + e.getMessage()));
        }
    }

    /**
     * Send quarterly report via email to a specific client.
     */
    @PostMapping("/quarterly/{clientId}/send-email")
    @Operation(
            summary = "Send quarterly report via email",
            description = "Generate and send quarterly report to client via email."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDTO<String>> sendQuarterlyReportEmail(
            @PathVariable Long clientId) {

        log.info("Email quarterly report requested for client: {}", clientId);

        try {
            QuarterlyReportDTO report = reportService.generateClientReportById(clientId);
            reportService.sendReportViaEmail(clientId, report);

            return ResponseEntity.ok(ApiResponseDTO.success(
                "Quarterly report sent via email successfully",
                "Report has been sent to the client's email address"));

        } catch (Exception e) {
            log.error("Failed to send quarterly report email for client {}: {}",
                clientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Failed to send quarterly report email: " + e.getMessage()));
        }
    }

    /**
     * Test email configuration.
     * Used to verify email settings are working correctly.
     */
    @GetMapping("/test-email")
    @Operation(
            summary = "Test email configuration",
            description = "Send a test email to verify email configuration is working."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Test email sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Email configuration error")
    })
    public ResponseEntity<ApiResponseDTO<String>> testEmailConfiguration() {

        log.info("Email configuration test requested");

        try {
            reportService.testEmailConfiguration();

            return ResponseEntity.ok(ApiResponseDTO.success(
                "Test email sent successfully",
                "Check your inbox for the test email"));

        } catch (Exception e) {
            log.error("Email configuration test failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.error("Email configuration test failed: " + e.getMessage()));
        }
    }
}
