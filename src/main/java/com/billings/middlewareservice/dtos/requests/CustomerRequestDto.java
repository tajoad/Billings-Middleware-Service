package com.billings.middlewareservice.dtos.requests;

import com.billings.middlewareservice.enums.CustomerTypes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerRequestDto (
    @NotNull(message = "Customer type is required (INDIVIDUAL or BUSINESS)")
    CustomerTypes customerType,

    @NotBlank(message = "Contact person or customer name is required")
    String name,

    String companyName,
    String tin,
    @NotBlank(message = "Address Line 1 is required")
    String addressLine1,
    String addressLine2,

    @NotBlank(message = "City is required")
    String city,

    @NotBlank(message = "State is required")
    String state,

    String country,

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    String email,

    @NotBlank(message = "Phone number is required")
    String phoneNumber
){}
