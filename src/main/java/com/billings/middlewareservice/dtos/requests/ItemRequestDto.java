package com.billings.middlewareservice.dtos.requests;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ItemRequestDto(

        @NotBlank(message = "Item name is required")
        @Size(max = 100, message = "Item name cannot exceed 100 characters")
        String name,
        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Unit price cannot be negative")
        @Digits(integer = 11, fraction = 4, message = "Unit price must fit within precision 15, scale 4")
        BigDecimal unitPrice,

        @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate cannot be negative")
        @Digits(integer = 3, fraction = 2, message = "Tax rate must fit within precision 5, scale 2")
        BigDecimal taxRate
) {
    public ItemRequestDto {
        if (taxRate == null) {
            taxRate = BigDecimal.ZERO;
        }
    }
}
