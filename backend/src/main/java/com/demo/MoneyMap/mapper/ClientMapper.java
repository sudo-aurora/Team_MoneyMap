package com.demo.MoneyMap.mapper;

import com.demo.MoneyMap.dto.request.ClientRequestDTO;
import com.demo.MoneyMap.dto.response.ClientResponseDTO;
import com.demo.MoneyMap.dto.response.PortfolioSummaryDTO;
import com.demo.MoneyMap.entity.Client;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for Client entity and DTOs.
 * Handles multi-country fields conversion.
 */
@Component
public class ClientMapper {

    public Client toEntity(ClientRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Client.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .stateOrProvince(dto.getStateOrProvince())
                .postalCode(dto.getPostalCode())
                .countryCode(dto.getCountryCode())
                .country(dto.getCountry())
                .preferredCurrency(dto.getPreferredCurrency())
                .timezone(dto.getTimezone())
                .locale(dto.getLocale())
                .active(true)
                .build();
    }

    public ClientResponseDTO toResponseDTO(Client entity) {
        if (entity == null) {
            return null;
        }

        return ClientResponseDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .city(entity.getCity())
                .stateOrProvince(entity.getStateOrProvince())
                .postalCode(entity.getPostalCode())
                .countryCode(entity.getCountryCode())
                .country(entity.getCountry())
                .preferredCurrency(entity.getPreferredCurrency())
                .timezone(entity.getTimezone())
                .locale(entity.getLocale())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ClientResponseDTO toResponseDTOWithPortfolios(Client entity) {
        ClientResponseDTO dto = toResponseDTO(entity);
        if (dto != null && entity.getPortfolio() != null) {
            List<PortfolioSummaryDTO> portfolios = new ArrayList<>();
            portfolios.add(PortfolioSummaryDTO.builder()
                    .id(entity.getPortfolio().getId())
                    .name(entity.getPortfolio().getName())
                    .description(entity.getPortfolio().getDescription())
                    .totalValue(entity.getPortfolio().getTotalValue())
                    .active(entity.getPortfolio().getActive())
                    .build());
            dto.setPortfolios(portfolios);
        }
        return dto;
    }

    public void updateEntityFromDTO(ClientRequestDTO dto, Client entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            entity.setAddress(dto.getAddress());
        }
        if (dto.getCity() != null) {
            entity.setCity(dto.getCity());
        }
        if (dto.getStateOrProvince() != null) {
            entity.setStateOrProvince(dto.getStateOrProvince());
        }
        if (dto.getPostalCode() != null) {
            entity.setPostalCode(dto.getPostalCode());
        }
        if (dto.getCountryCode() != null) {
            entity.setCountryCode(dto.getCountryCode());
        }
        if (dto.getCountry() != null) {
            entity.setCountry(dto.getCountry());
        }
        if (dto.getPreferredCurrency() != null) {
            entity.setPreferredCurrency(dto.getPreferredCurrency());
        }
        if (dto.getTimezone() != null) {
            entity.setTimezone(dto.getTimezone());
        }
        if (dto.getLocale() != null) {
            entity.setLocale(dto.getLocale());
        }
    }
}
