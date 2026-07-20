package com.billings.middlewareservice.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InvoiceResponseDto(
        UUID id,
        String invoiceNumber, // Auto-generated e.g., INV-00001
        UUID customerId,
        Instant invoiceDate,
        List<InvoiceLineResponseDto> lines,
        BigDecimal totalSubTotal, // Sum of line subTotals
        BigDecimal totalTax,      // Sum of line taxAmounts
        BigDecimal grandTotal ,    // totalSubTotal + totalTax
        List<InvoicePaymentHistoryDto> paymentHistory
) {
}
