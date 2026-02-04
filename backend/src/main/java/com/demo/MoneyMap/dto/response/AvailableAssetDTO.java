package com.demo.MoneyMap.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Available Asset response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailableAssetDTO {
    private String symbol;
    private String name;
    private String assetType;
    private BigDecimal currentMarketPrice;
    private String exchangeOrNetwork;
}
