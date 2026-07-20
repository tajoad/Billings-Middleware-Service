package com.billings.middlewareservice.repositories;

import com.billings.middlewareservice.entities.Customer;
import com.billings.middlewareservice.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    @Query("SELECT i.invoiceNumber FROM Invoice i WHERE i.invoiceNumber LIKE 'INV-%' ORDER BY i.invoiceNumber DESC LIMIT 1")
    Optional<String> findLastInvoiceNumber();
    List<Invoice> findByCustomerId(UUID customerId);
    boolean existsByInvoiceNumber(String invoiceNumber);

}
