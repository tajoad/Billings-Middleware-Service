package com.billings.middlewareservice.entities;

import com.billings.middlewareservice.enums.PaymentMethod;
import com.billings.middlewareservice.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "payment_reference", unique = true, nullable = false)
    private String paymentReference;

    @Column(name = "amount_paid", nullable = false, precision = 15, scale = 4)
    private BigDecimal amountPaid;

    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod; // CARD, TRANSFER, USSD

    @Column(nullable = false)
    private PaymentStatus status; // PENDING, SUCCESSFUL, FAILED

    @Column(name = "processed_at")
    private Instant processedAt;
}