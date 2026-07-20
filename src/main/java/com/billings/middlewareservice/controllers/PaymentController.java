package com.billings.middlewareservice.controllers;

import com.billings.middlewareservice.dtos.requests.PaymentRequestDto;
import com.billings.middlewareservice.dtos.response.ApiResponseDto;
import com.billings.middlewareservice.dtos.response.PaymentResponseDto;
import com.billings.middlewareservice.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponseDto<PaymentResponseDto>> processPayment(@Valid @RequestBody PaymentRequestDto requestDto) {
        PaymentResponseDto response = paymentService.processInvoicePayment(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Payment generated successfully", response));
    }
}
