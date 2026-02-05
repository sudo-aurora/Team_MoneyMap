package com.demo.MoneyMap.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for creating or updating a client.
 * Includes multi-country support fields.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a client")
public class ClientRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(description = "Client's first name", example = "John", required = true)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(description = "Client's last name", example = "Doe", required = true)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    @Schema(description = "Client's email address", example = "john.doe@example.com", required = true)
    private String email;

    @Size(max = 30, message = "Phone must not exceed 30 characters")
    @Schema(description = "Client's phone number with country code", example = "+1-555-123-4567")
    private String phone;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Schema(description = "Client's full street address", example = "123 Wall Street")
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Schema(description = "City", example = "New York")
    private String city;

    @Size(max = 100, message = "State/Province must not exceed 100 characters")
    @Schema(description = "State or Province", example = "New York")
    private String stateOrProvince;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Schema(description = "Postal/ZIP code", example = "10005")
    private String postalCode;

    @Size(max = 2, message = "Country code must be 2 characters (ISO 3166-1 alpha-2)")
    @Schema(description = "Country code (ISO 3166-1 alpha-2)", example = "US")
    private String countryCode;

    @Size(max = 100, message = "Country name must not exceed 100 characters")
    @Schema(description = "Full country name", example = "United States")
    private String country;

    @Size(max = 3, message = "Currency code must be 3 characters (ISO 4217)")
    @Schema(description = "Preferred currency (ISO 4217)", example = "USD")
    private String preferredCurrency;

    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    @Schema(description = "IANA timezone", example = "America/New_York")
    private String timezone;

    @Size(max = 10, message = "Locale must not exceed 10 characters")
    @Schema(description = "Locale for language/formatting", example = "en_US")
    private String locale;

    @Schema(description = "Initial wallet balance for trading", example = "10000.00")
    private java.math.BigDecimal walletBalance;
}
