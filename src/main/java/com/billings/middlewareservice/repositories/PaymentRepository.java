package com.billings.middlewareservice.repositories;

import com.billings.middlewareservice.entities.Invoice;
import com.billings.middlewareservice.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

}
