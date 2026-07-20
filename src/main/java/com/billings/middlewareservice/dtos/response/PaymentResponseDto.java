package com.billings.middlewareservice.dtos.response;

import com.billings.middlewareservice.enums.InvoiceStatus;
import com.billings.middlewareservice.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponseDto(
        UUID paymentId,
        UUID invoiceId,
        BigDecimal amountPaid,

        BigDecimal totalPaid,

        BigDecimal remainingBalance,
        PaymentMethod paymentMethod,
        InvoiceStatus updatedInvoiceStatus,
        Instant processedAt
) {
}
