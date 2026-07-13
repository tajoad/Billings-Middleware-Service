package com.billings.middlewareservice.dtos.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemResponseDto(
        UUID id,
        String itemCode,
        String name,
        String description,
        BigDecimal unitPrice,
        BigDecimal taxRate
) {
}
