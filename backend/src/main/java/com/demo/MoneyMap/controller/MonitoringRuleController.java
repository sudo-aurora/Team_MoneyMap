package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.dto.request.MonitoringRuleRequestDTO;
import com.demo.MoneyMap.dto.response.ApiResponseDTO;
import com.demo.MoneyMap.dto.response.MonitoringRuleResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.enums.RuleType;
import com.demo.MoneyMap.service.MonitoringRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Monitoring Rule management operations.
 * Handles CRUD operations for transaction monitoring rules.
 */
@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
@Tag(name = "Monitoring Rules", description = "APIs for managing transaction monitoring rules. " +
        "Rules define conditions that trigger alerts when suspicious transaction patterns are detected. " +
        "Supports AMOUNT_THRESHOLD, VELOCITY, NEW_PAYEE, and DAILY_LIMIT rule types.")
public class MonitoringRuleController {

    private final MonitoringRuleService ruleService;

    @PostMapping
    @Operation(summary = "Create a new monitoring rule", description = "Creates a new monitoring rule. " +
            "The rule will immediately start evaluating new transactions if marked as active. " +
            "Configure appropriate parameters based on rule type:\n" +
            "- AMOUNT_THRESHOLD: thresholdAmount, thresholdCurrency\n" +
            "- VELOCITY: maxTransactions, timeWindowMinutes\n" +
            "- NEW_PAYEE: lookbackDays\n" +
            "- DAILY_LIMIT: dailyLimitAmount")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rule created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rule data"),
            @ApiResponse(responseCode = "409", description = "Rule with same name already exists")
    })
    public ResponseEntity<ApiResponseDTO<MonitoringRuleResponseDTO>> createRule(
            @Valid @RequestBody MonitoringRuleRequestDTO requestDTO) {
        MonitoringRuleResponseDTO rule = ruleService.createRule(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(rule, "Rule created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rule by ID", description = "Retrieves a monitoring rule by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rule found"),
            @ApiResponse(responseCode = "404", description = "Rule not found")
    })
    public ResponseEntity<ApiResponseDTO<MonitoringRuleResponseDTO>> getRuleById(@PathVariable Long id) {
        MonitoringRuleResponseDTO rule = ruleService.getRuleById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(rule));
    }

    @GetMapping
    @Operation(summary = "Get all rules", description = "Retrieves a paginated list of all monitoring rules.")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<MonitoringRuleResponseDTO>>> getAllRules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponseDTO<MonitoringRuleResponseDTO> rules = ruleService.getAllRules(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(rules));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active rules", description = "Retrieves all currently active monitoring rules.")
    public ResponseEntity<ApiResponseDTO<List<MonitoringRuleResponseDTO>>> getActiveRules() {
        List<MonitoringRuleResponseDTO> rules = ruleService.getActiveRules();
        return ResponseEntity.ok(ApiResponseDTO.success(rules));
    }

    @GetMapping("/type/{ruleType}")
    @Operation(summary = "Get rules by type", description = "Retrieves rules filtered by their type. " +
            "Valid types: AMOUNT_THRESHOLD, VELOCITY, NEW_PAYEE, DAILY_LIMIT")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<MonitoringRuleResponseDTO>>> getRulesByType(
            @PathVariable RuleType ruleType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponseDTO<MonitoringRuleResponseDTO> rules = ruleService.getRulesByType(ruleType, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(rules));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a rule", description = "Updates an existing monitoring rule. " +
            "Changes take effect immediately for new transactions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rule updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rule data"),
            @ApiResponse(responseCode = "404", description = "Rule not found"),
            @ApiResponse(responseCode = "409", description = "Rule with same name already exists")
    })
    public ResponseEntity<ApiResponseDTO<MonitoringRuleResponseDTO>> updateRule(
            @PathVariable Long id,
            @Valid @RequestBody MonitoringRuleRequestDTO requestDTO) {
        MonitoringRuleResponseDTO rule = ruleService.updateRule(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(rule, "Rule updated successfully"));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a rule", description = "Activates a deactivated rule. " +
            "The rule will start evaluating new transactions.")
    public ResponseEntity<ApiResponseDTO<MonitoringRuleResponseDTO>> activateRule(@PathVariable Long id) {
        MonitoringRuleResponseDTO rule = ruleService.activateRule(id);
        return ResponseEntity.ok(ApiResponseDTO.success(rule, "Rule activated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a rule", description = "Deactivates an active rule. " +
            "The rule will stop evaluating new transactions but existing alerts remain.")
    public ResponseEntity<ApiResponseDTO<MonitoringRuleResponseDTO>> deactivateRule(@PathVariable Long id) {
        MonitoringRuleResponseDTO rule = ruleService.deactivateRule(id);
        return ResponseEntity.ok(ApiResponseDTO.success(rule, "Rule deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a rule", description = "Permanently deletes a monitoring rule. " +
            "Consider deactivating instead to preserve alert history.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rule deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Rule not found")
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Rule deleted successfully"));
    }

    @GetMapping("/types")
    @Operation(summary = "Get all rule types", description = "Returns a list of all supported monitoring rule types with descriptions.")
    public ResponseEntity<ApiResponseDTO<List<RuleType>>> getRuleTypes() {
        List<RuleType> ruleTypes = ruleService.getRuleTypes();
        return ResponseEntity.ok(ApiResponseDTO.success(ruleTypes));
    }
}
