package com.billings.middlewareservice.services;

import com.billings.middlewareservice.dtos.requests.CustomerRequestDto;
import com.billings.middlewareservice.dtos.requests.ItemRequestDto;
import com.billings.middlewareservice.dtos.response.CustomerResponseDto;
import com.billings.middlewareservice.dtos.response.ItemResponseDto;

import java.util.List;
import java.util.UUID;

public interface ItemService {
    ItemResponseDto createItem(ItemRequestDto request);
    ItemResponseDto getItemById(UUID id);
    List<ItemResponseDto> getAllItems();

    ItemResponseDto updateItems(UUID id, ItemRequestDto request);
    void deleteItem(UUID id);
}
