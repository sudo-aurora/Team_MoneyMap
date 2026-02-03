package com.demo.MoneyMap.dto.response;

import com.demo.MoneyMap.entity.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for payment status history entry.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payment status history entry")
public class PaymentStatusHistoryDTO {

    @Schema(description = "History entry ID", example = "1")
    private Long id;

    @Schema(description = "Previous status", example = "CREATED")
    private PaymentStatus previousStatus;

    @Schema(description = "New status", example = "VALIDATED")
    private PaymentStatus status;

    @Schema(description = "Status display name", example = "Validated")
    private String statusDisplayName;

    @Schema(description = "Notes for this status change")
    private String notes;

    @Schema(description = "Timestamp of the status change")
    private LocalDateTime timestamp;
}
