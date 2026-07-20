package com.billings.middlewareservice.services;

import com.billings.middlewareservice.dtos.requests.PaymentRequestDto;
import com.billings.middlewareservice.dtos.response.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto processInvoicePayment(PaymentRequestDto requestDto);
}
