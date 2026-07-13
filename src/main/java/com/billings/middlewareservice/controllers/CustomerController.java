package com.billings.middlewareservice.controllers;

import com.billings.middlewareservice.dtos.requests.CustomerRequestDto;
import com.billings.middlewareservice.dtos.response.ApiResponseDto;
import com.billings.middlewareservice.dtos.response.CustomerResponseDto;
import com.billings.middlewareservice.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAuthority('customer:setup')")
    public ResponseEntity<ApiResponseDto<CustomerResponseDto>> createCustomer(
            @Valid @RequestBody CustomerRequestDto request) {

        CustomerResponseDto createdCustomer = customerService.createCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Customer profile set up successfully.", createdCustomer));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('customer:read', 'invoice:create')")
    public ResponseEntity<ApiResponseDto<CustomerResponseDto>> getCustomerById(
            @PathVariable UUID id) {

        CustomerResponseDto customer = customerService.getCustomerById(id);

        return ResponseEntity.ok(
                ApiResponseDto.success("Customer record retrieved successfully.", customer)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('customer:read', 'invoice:create')")
    public ResponseEntity<ApiResponseDto<List<CustomerResponseDto>>> getAllActiveCustomers() {

        List<CustomerResponseDto> customers = customerService.getAllActiveCustomers();

        return ResponseEntity.ok(
                ApiResponseDto.success("Active customer base directory loaded successfully.", customers)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:setup')")
    public ResponseEntity<ApiResponseDto<CustomerResponseDto>> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequestDto request) {

        CustomerResponseDto updatedCustomer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(
                ApiResponseDto.success("Customer profile updated successfully.", updatedCustomer)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:setup')")
    public ResponseEntity<ApiResponseDto<Void>> deleteCustomer(@PathVariable UUID id) {

        customerService.deleteCustomer(id);
        return ResponseEntity.ok(
                ApiResponseDto.success("Customer account deactivated successfully.", null)
        );
    }
}
