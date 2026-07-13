package com.billings.middlewareservice.dtos.response;

import com.billings.middlewareservice.enums.CustomerTypes;

import java.util.UUID;

public record CustomerResponseDto(
        UUID id,
        String customerCode, // e.g., CUST-1001
        CustomerTypes customerType,
        String name,
        String companyName,
        String tin,
        String fullAddress,
        String email,
        String phoneNumber,
        Boolean isActive
) {
}
