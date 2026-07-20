package com.billings.middlewareservice.dtos.requests;

import com.billings.middlewareservice.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestDto(
        @NotNull(message = "Invoice ID is required")
        UUID invoiceId,

        @NotNull(message = "Payment amount is required")
        @DecimalMin(value = "0.01", message = "Payment amount must be at least 0.01")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {
}
