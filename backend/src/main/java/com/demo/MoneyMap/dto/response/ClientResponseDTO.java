package com.demo.MoneyMap.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for client response.
 * Includes multi-country support fields.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Client response data")
public class ClientResponseDTO {

    @Schema(description = "Client ID", example = "1")
    private Long id;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Full name", example = "John Doe")
    private String fullName;

    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Phone number", example = "+1-555-123-4567")
    private String phone;

    @Schema(description = "Street address", example = "123 Wall Street")
    private String address;

    @Schema(description = "City", example = "New York")
    private String city;

    @Schema(description = "State or Province", example = "New York")
    private String stateOrProvince;

    @Schema(description = "Postal/ZIP code", example = "10005")
    private String postalCode;

    @Schema(description = "Country code (ISO 3166-1 alpha-2)", example = "US")
    private String countryCode;

    @Schema(description = "Full country name", example = "United States")
    private String country;

    @Schema(description = "Preferred currency (ISO 4217)", example = "USD")
    private String preferredCurrency;

    @Schema(description = "IANA timezone", example = "America/New_York")
    private String timezone;

    @Schema(description = "Locale", example = "en_US")
    private String locale;

    @Schema(description = "Whether the client is active", example = "true")
    private Boolean active;

    @Schema(description = "When the client was created")
    private LocalDateTime createdAt;

    @Schema(description = "When the client was last updated")
    private LocalDateTime updatedAt;

    @Schema(description = "Client's portfolios (OneToOne - will have 0 or 1 portfolio)")
    private List<PortfolioSummaryDTO> portfolios;
}
