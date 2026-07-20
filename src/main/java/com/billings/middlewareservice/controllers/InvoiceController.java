package com.billings.middlewareservice.controllers;

import com.billings.middlewareservice.dtos.requests.InvoiceRequestDto;
import com.billings.middlewareservice.dtos.response.ApiResponseDto;
import com.billings.middlewareservice.dtos.response.InvoiceResponseDto;
import com.billings.middlewareservice.services.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING')")
    public ResponseEntity<ApiResponseDto<InvoiceResponseDto>> createInvoice(
            @Valid @RequestBody InvoiceRequestDto requestDto) {

        InvoiceResponseDto response = invoiceService.createInvoice(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Invoice generated successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING', 'USER')")
    public ResponseEntity<ApiResponseDto<InvoiceResponseDto>> getInvoiceById(@PathVariable UUID id) {
        InvoiceResponseDto response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponseDto.success("Invoice retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING')")
    public ResponseEntity<ApiResponseDto<List<InvoiceResponseDto>>> getAllInvoices() {
        List<InvoiceResponseDto> response = invoiceService.getAllInvoices();
        return ResponseEntity.ok(ApiResponseDto.success("All invoices retrieved successfully", response));
    }
}
