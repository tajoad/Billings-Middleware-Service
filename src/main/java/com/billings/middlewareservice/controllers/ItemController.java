package com.billings.middlewareservice.controllers;

import com.billings.middlewareservice.dtos.requests.CustomerRequestDto;
import com.billings.middlewareservice.dtos.requests.ItemRequestDto;
import com.billings.middlewareservice.dtos.response.ApiResponseDto;
import com.billings.middlewareservice.dtos.response.CustomerResponseDto;
import com.billings.middlewareservice.dtos.response.ItemResponseDto;
import com.billings.middlewareservice.services.CustomerService;
import com.billings.middlewareservice.services.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @PreAuthorize("hasAuthority('item:setup')")
    public ResponseEntity<ApiResponseDto<ItemResponseDto>> createItem(
            @Valid @RequestBody ItemRequestDto requestDto) {

        ItemResponseDto response = itemService.createItem(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Item created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('item:read')")
    public ResponseEntity<ApiResponseDto<ItemResponseDto>> getItemById(@PathVariable UUID id) {
        ItemResponseDto response = itemService.getItemById(id);
        return ResponseEntity.ok(ApiResponseDto.success("Item retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('item:read')")
    public ResponseEntity<ApiResponseDto<List<ItemResponseDto>>> getAllItems() {
        List<ItemResponseDto> response = itemService.getAllItems();
        return ResponseEntity.ok(ApiResponseDto.success("All items retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('item:setup')")
    public ResponseEntity<ApiResponseDto<ItemResponseDto>> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody ItemRequestDto requestDto) {

        ItemResponseDto response = itemService.updateItems(id, requestDto);
        return ResponseEntity.ok(ApiResponseDto.success("Item updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('item:setup')")
    public ResponseEntity<ApiResponseDto<Void>> deleteItem(@PathVariable UUID id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(ApiResponseDto.success("Item deleted successfully", null));
    }
}
