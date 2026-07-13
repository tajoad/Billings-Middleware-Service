package com.billings.middlewareservice.repositories;

import com.billings.middlewareservice.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findByIsActiveTrue();

    boolean existsByEmailAndIsActiveTrue(String email);
    boolean existsByTinAndIsActiveTrue(String tin);

    Optional<Customer> findByCustomerCode(String customerCode);
}
