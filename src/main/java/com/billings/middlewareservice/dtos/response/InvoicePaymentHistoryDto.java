package com.billings.middlewareservice.dtos.response;

import com.billings.middlewareservice.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InvoicePaymentHistoryDto(
        UUID paymentId,
        String paymentReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Instant processedAt
) {
}
