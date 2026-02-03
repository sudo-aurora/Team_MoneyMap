package com.demo.MoneyMap.dto.request;

import com.demo.MoneyMap.entity.enums.AlertSeverity;
import com.demo.MoneyMap.entity.enums.RuleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a monitoring rule.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a monitoring rule")
public class MonitoringRuleRequestDTO {

    @NotBlank(message = "Rule name is required")
    @Size(max = 100, message = "Rule name cannot exceed 100 characters")
    @Schema(description = "Name of the rule", example = "High Value Transaction", required = true)
    private String ruleName;

    @NotNull(message = "Rule type is required")
    @Schema(description = "Type of the rule", example = "AMOUNT_THRESHOLD", required = true)
    private RuleType ruleType;

    @NotNull(message = "Severity is required")
    @Schema(description = "Alert severity when rule triggers", example = "HIGH", required = true)
    private AlertSeverity severity;

    @Schema(description = "Whether the rule is active", example = "true")
    private Boolean active = true;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Rule description", example = "Triggers when transaction amount exceeds $10,000")
    private String description;

    // Amount Threshold Rule parameters
    @DecimalMin(value = "0.01", message = "Threshold amount must be greater than 0")
    @Schema(description = "Threshold amount for AMOUNT_THRESHOLD rule", example = "10000.00")
    private BigDecimal thresholdAmount;

    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    @Schema(description = "Currency for threshold", example = "USD")
    private String thresholdCurrency;

    // Velocity Rule parameters
    @Min(value = 1, message = "Max transactions must be at least 1")
    @Schema(description = "Maximum transactions for VELOCITY rule", example = "5")
    private Integer maxTransactions;

    @Min(value = 1, message = "Time window must be at least 1 minute")
    @Schema(description = "Time window in minutes for VELOCITY rule", example = "10")
    private Integer timeWindowMinutes;

    // Daily Limit Rule parameters
    @DecimalMin(value = "0.01", message = "Daily limit amount must be greater than 0")
    @Schema(description = "Daily limit amount for DAILY_LIMIT rule", example = "50000.00")
    private BigDecimal dailyLimitAmount;

    // New Payee Rule parameters
    @Min(value = 1, message = "Lookback days must be at least 1")
    @Schema(description = "Lookback days for NEW_PAYEE rule", example = "90")
    private Integer lookbackDays;
}
