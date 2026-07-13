package com.billings.middlewareservice.entities;

import com.billings.middlewareservice.enums.CustomerTypes;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Handled natively by PostgreSQL sequence on insert
    @Column(name = "customer_code", insertable = false, updatable = false, unique = true, nullable = false)
    private String customerCode;

    @Column(name = "customer_type", nullable = false)
    private CustomerTypes customerType; // "INDIVIDUAL" or "BUSINESS"

    @Column(nullable = false)
    private String name;

    @Column(name = "company_name")
    private String companyName;

    private String tin;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    private String city;
    private String state;
    private String country;
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}