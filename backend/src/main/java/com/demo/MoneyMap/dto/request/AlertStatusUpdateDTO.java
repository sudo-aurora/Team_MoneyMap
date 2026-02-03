package com.demo.MoneyMap.dto.request;

import com.demo.MoneyMap.entity.enums.AlertStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for updating alert status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for updating alert status")
public class AlertStatusUpdateDTO {

    @NotNull(message = "Status is required")
    @Schema(description = "New alert status", example = "ACKNOWLEDGED", required = true)
    private AlertStatus status;

    @Size(max = 100, message = "Operator name cannot exceed 100 characters")
    @Schema(description = "Name of the operator performing the action", example = "John Doe")
    private String operatorName;

    @Size(max = 1000, message = "Resolution notes cannot exceed 1000 characters")
    @Schema(description = "Resolution notes (required for CLOSED status)", example = "Verified as legitimate transaction")
    private String resolutionNotes;
}
