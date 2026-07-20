package com.billings.middlewareservice.dtos.response;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceLineResponseDto(
        UUID id,
        UUID itemId,
        String itemCode,
        String itemName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal taxRate,
        BigDecimal subTotal, // quantity * unitPrice
        BigDecimal taxAmount, // subTotal * taxRate
        BigDecimal total     // subTotal + taxAmount
) {
}
