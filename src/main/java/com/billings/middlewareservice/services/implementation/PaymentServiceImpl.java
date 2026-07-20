package com.billings.middlewareservice.services.implementation;

import com.billings.middlewareservice.dtos.requests.PaymentRequestDto;
import com.billings.middlewareservice.dtos.response.PaymentResponseDto;
import com.billings.middlewareservice.entities.Invoice;
import com.billings.middlewareservice.entities.Payment;
import com.billings.middlewareservice.enums.InvoiceStatus;
import com.billings.middlewareservice.enums.PaymentStatus;
import com.billings.middlewareservice.exceptions.ResourceNotFoundException;
import com.billings.middlewareservice.repositories.InvoiceRepository;
import com.billings.middlewareservice.repositories.PaymentRepository;
import com.billings.middlewareservice.services.PaymentService;
import com.billings.middlewareservice.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    @Override
    public PaymentResponseDto processInvoicePayment(PaymentRequestDto requestDto) {

        Invoice invoice = invoiceRepository.findById(requestDto.invoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        // 2. Compute current outstanding balances
        BigDecimal currentBalance = invoice.getNetAmount().subtract(invoice.getAmountPaid());

        if (currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Invoice is already fully paid.");
        }

        if (requestDto.amount().compareTo(currentBalance) > 0) {
            throw new IllegalArgumentException("Payment details exceed remaining balance.");
        }

        // 3. Instantiate the new transactional payment record
        Payment payment = Payment.builder()
                .amountPaid(requestDto.amount())
                .paymentReference(ReferenceGenerator.generatePaymentReference())
                .status(PaymentStatus.SUCCESSFUL)
                .paymentMethod(requestDto.paymentMethod())
                .processedAt(Instant.now())
                .build();

        // 4. Link via helper to manage bidirectional references safely
        invoice.addPayment(payment);

        // 5. Increment internal accumulator column
        BigDecimal updatedTotalPaid = invoice.getAmountPaid().add(requestDto.amount());
        invoice.setAmountPaid(updatedTotalPaid);

        // 6. Transition statuses state dynamically
        BigDecimal finalRemaining = invoice.getNetAmount().subtract(updatedTotalPaid);
        if (finalRemaining.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        return new PaymentResponseDto(
                payment.getId(),
                invoice.getId(),
                requestDto.amount(),
                updatedTotalPaid,
                finalRemaining,
                requestDto.paymentMethod(),
                invoice.getStatus(),
                payment.getProcessedAt()
        );
    }
}
