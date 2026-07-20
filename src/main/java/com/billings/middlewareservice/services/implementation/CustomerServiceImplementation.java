package com.billings.middlewareservice.services.implementation;

import com.billings.middlewareservice.dtos.requests.CustomerRequestDto;
import com.billings.middlewareservice.dtos.response.CustomerResponseDto;
import com.billings.middlewareservice.dtos.response.ItemResponseDto;
import com.billings.middlewareservice.entities.Customer;
import com.billings.middlewareservice.enums.CustomerTypes;
import com.billings.middlewareservice.event.AuditEvent;
import com.billings.middlewareservice.repositories.CustomerRepository;
import com.billings.middlewareservice.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImplementation implements CustomerService {

    public final CustomerRepository customerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto request) {
        if (request.customerType() == CustomerTypes.BUSINESS) {
            if (request.companyName() == null || request.companyName().isBlank()) {
                throw new IllegalArgumentException ("Company Name is required for corporate customers.");
            }
            if (request.tin() == null || request.tin().isBlank()) {
                throw new IllegalArgumentException("Tax Identification Number (TIN) is required for corporate customers.");
            }
        }

        // Build the transactional Entity object
        Customer customer = Customer.builder()
                .customerType(request.customerType())
                .name(request.name())
                .companyName(request.customerType() == CustomerTypes.BUSINESS ? request.companyName() : null)
                .tin(request.customerType() == CustomerTypes.BUSINESS ? request.tin() : null)
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .city(request.city())
                .state(request.state())
                .country(request.country() == null || request.country().isBlank() ? "Nigeria" : request.country())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        eventPublisher.publishEvent(new AuditEvent(
                "CUSTOMER",
                savedCustomer.getId(),
                "INSERT",
                savedCustomer.getCreatedBy(),
                null,
                savedCustomer
        ));
        return mapToCustomerResponse(savedCustomer);
    }

    @Override
    public CustomerResponseDto getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer record not found for ID: " + id));
        return mapToCustomerResponse(customer);
    }

    @Override
    public List<CustomerResponseDto> getAllActiveCustomers() {
        return customerRepository.findByIsActiveTrue().stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDto updateCustomer(UUID id, CustomerRequestDto request) {
        Customer customer = customerRepository.findById(id)
                .filter(Customer::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Active customer record not found for ID: " + id));

        if (request.customerType() == CustomerTypes.BUSINESS) {
            if (request.companyName() == null || request.companyName().isBlank()) {
                throw new IllegalArgumentException("Company Name is required for corporate customers.");
            }
            if (request.tin() == null || request.tin().isBlank()) {
                throw new IllegalArgumentException("Tax Identification Number (TIN) is required for corporate customers.");
            }
        }

        CustomerResponseDto oldDto = mapToCustomerResponse(customer);
        Map<String, Object> oldStateSnapshot = objectMapper.convertValue(oldDto, java.util.Map.class);

        customer.setName(request.name());
        customer.setCustomerType(request.customerType());
        customer.setCompanyName(request.customerType() == CustomerTypes.BUSINESS ? request.companyName() : null);
        customer.setTin(request.customerType() == CustomerTypes.BUSINESS ? request.tin() : null);
        customer.setAddressLine1(request.addressLine1());
        customer.setAddressLine2(request.addressLine2());
        customer.setCity(request.city());
        customer.setState(request.state());
        customer.setCountry(request.country());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());

        Customer updatedCustomer = customerRepository.save(customer);


        // Fire off an async event here to capture old vs new JSON structures inside audit_logs

        eventPublisher.publishEvent(new AuditEvent(
                "CUSTOMER",
                updatedCustomer.getId(),
                "UPDATE",
                updatedCustomer.getUpdatedBy(),
                oldStateSnapshot,
                updatedCustomer
        ));

        return mapToCustomerResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .filter(Customer::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Active customer record not found for ID: " + id));

        // Perform Soft Delete
        customer.setActive(false);
        customer.setDeletedAt(java.time.Instant.now());

        Customer deletedCustomer = customerRepository.save(customer);

        // Fetch currently authenticated operator ID context
        String performanceOperatorId = deletedCustomer.getUpdatedBy();


        // Fire off async tracking event: Action='DELETE' into central ledger
        eventPublisher.publishEvent(new AuditEvent(
                "CUSTOMER",
                deletedCustomer.getId(),
                "DELETE",
                performanceOperatorId,
                java.util.Map.of("isActive", true),
                java.util.Map.of("isActive", false, "deletedAt", deletedCustomer.getDeletedAt())
        ));
    }

    private CustomerResponseDto mapToCustomerResponse(Customer customer) {
        // Combine fields into a clean readable string format for UI consumption
        String fullAddress = String.format("%s, %s, %s, %s",
                customer.getAddressLine1(),
                customer.getCity(),
                customer.getState(),
                customer.getCountry() != null ? customer.getCountry() : "Nigeria");

        return new CustomerResponseDto(
                customer.getId(),
                customer.getCustomerCode(),
                customer.getCustomerType(),
                customer.getName(),
                customer.getCompanyName(),
                customer.getTin(),
                fullAddress,
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.isActive()
        );
    }
}
