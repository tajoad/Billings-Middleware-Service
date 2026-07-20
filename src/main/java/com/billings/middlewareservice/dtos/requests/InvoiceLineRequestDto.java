package com.billings.middlewareservice.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InvoiceLineRequestDto(
        @NotNull(message = "Item ID is required")
        UUID itemId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {
}
