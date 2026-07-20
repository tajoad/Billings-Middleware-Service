package com.billings.middlewareservice.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record InvoiceRequestDto(
        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @NotEmpty(message = "Invoice must contain at least one line item")
        List<InvoiceLineRequestDto> lines
) {
}
