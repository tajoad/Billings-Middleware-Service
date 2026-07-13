package com.billings.middlewareservice.services;

import com.billings.middlewareservice.dtos.requests.CustomerRequestDto;
import com.billings.middlewareservice.dtos.response.CustomerResponseDto;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponseDto createCustomer(CustomerRequestDto request);
    CustomerResponseDto getCustomerById(UUID id);
    List<CustomerResponseDto> getAllActiveCustomers();

    CustomerResponseDto updateCustomer(UUID id, CustomerRequestDto request);
    void deleteCustomer(UUID id);
}
