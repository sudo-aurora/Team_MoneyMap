package com.demo.MoneyMap.dto.request;

import com.demo.MoneyMap.entity.enums.PaymentErrorCode;
import com.demo.MoneyMap.entity.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for updating payment status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for updating payment status")
public class PaymentStatusUpdateDTO {

    @NotNull(message = "Status is required")
    @Schema(description = "New payment status", example = "VALIDATED", required = true)
    private PaymentStatus status;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Schema(description = "Notes for the status change", example = "All validation checks passed")
    private String notes;

    @Schema(description = "Error code if marking as FAILED")
    private PaymentErrorCode errorCode;

    @Size(max = 500, message = "Error message cannot exceed 500 characters")
    @Schema(description = "Error message if marking as FAILED")
    private String errorMessage;
}
