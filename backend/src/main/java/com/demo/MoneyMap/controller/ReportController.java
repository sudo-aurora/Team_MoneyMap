package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.dto.response.ApiResponseDTO;
import com.demo.MoneyMap.dto.response.QuarterlyReportDTO;
//import com.demo.MoneyMap.service.ReportService;
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
//
//    private final ReportService reportService;
//
//    /**
//     * Generate quarterly report for a specific client.
//     * Used for manual report generation or testing.
//     */
//    @GetMapping("/quarterly/{clientId}")
//    @Operation(
//            summary = "Generate quarterly report for client",
//            description = "Generate a comprehensive quarterly report for a specific client including portfolio metrics, asset breakdown, and transaction history."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Quarterly report generated successfully"),
//            @ApiResponse(responseCode = "404", description = "Client not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<ApiResponseDTO<QuarterlyReportDTO>> generateQuarterlyReport(
//            @PathVariable Long clientId) {
//
//        log.info("Manual quarterly report generation requested for client: {}", clientId);
//
//        try {
//            // Get client and generate report
//            QuarterlyReportDTO report = reportService.generateClientReportById(clientId);
//
//            return ResponseEntity.ok(ApiResponseDTO.success(report,
//                "Quarterly report generated successfully"));
//
//        } catch (Exception e) {
//            log.error("Failed to generate quarterly report for client {}: {}",
//                clientId, e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponseDTO.error("Failed to generate quarterly report: " + e.getMessage()));
//        }
//    }
//
//    /**
//     * Trigger quarterly reports for all active clients.
//     * Used to manually trigger the scheduled quarterly report generation.
//     */
//    @PostMapping("/quarterly/generate-all")
//    @Operation(
//            summary = "Generate quarterly reports for all clients",
//            description = "Manually trigger the quarterly report generation process for all active clients."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Quarterly reports generation started"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<ApiResponseDTO<String>> generateQuarterlyReportsForAll() {
//
//        log.info("Manual quarterly report generation triggered for all clients");
//
//        try {
//            reportService.generateQuarterlyReports();
//
//            return ResponseEntity.ok(ApiResponseDTO.success(
//                "Quarterly report generation started for all active clients",
//                "Reports will be sent via email"));
//
//        } catch (Exception e) {
//            log.error("Failed to start quarterly report generation: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponseDTO.error("Failed to start quarterly report generation: " + e.getMessage()));
//        }
//    }
//
//    /**
//     * Test email configuration.
//     * Used to verify email settings are working correctly.
//     */
//    @GetMapping("/test-email")
//    @Operation(
//            summary = "Test email configuration",
//            description = "Send a test email to verify email configuration is working."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Test email sent successfully"),
//            @ApiResponse(responseCode = "500", description = "Email configuration error")
//    })
//    public ResponseEntity<ApiResponseDTO<String>> testEmailConfiguration() {
//
//        log.info("Email configuration test requested");
//
//        try {
//            reportService.testEmailConfiguration();
//
//            return ResponseEntity.ok(ApiResponseDTO.success(
//                "Test email sent successfully",
//                "Check your inbox for the test email"));
//
//        } catch (Exception e) {
//            log.error("Email configuration test failed: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponseDTO.error("Email configuration test failed: " + e.getMessage()));
//        }
//    }
}
