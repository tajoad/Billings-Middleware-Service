package com.billings.middlewareservice.services;

import com.billings.middlewareservice.dtos.requests.InvoiceRequestDto;
import com.billings.middlewareservice.dtos.response.InvoiceResponseDto;

import java.util.List;
import java.util.UUID;

public interface InvoiceService {
    InvoiceResponseDto createInvoice(InvoiceRequestDto requestDto);
    InvoiceResponseDto getInvoiceById(UUID id);
    List<InvoiceResponseDto> getAllInvoices();
}
