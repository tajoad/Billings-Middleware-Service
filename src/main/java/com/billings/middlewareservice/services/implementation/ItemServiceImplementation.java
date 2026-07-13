package com.billings.middlewareservice.services.implementation;

import com.billings.middlewareservice.dtos.requests.ItemRequestDto;
import com.billings.middlewareservice.dtos.response.ItemResponseDto;
import com.billings.middlewareservice.entities.Customer;
import com.billings.middlewareservice.entities.Item;
import com.billings.middlewareservice.event.AuditEvent;
import com.billings.middlewareservice.repositories.ItemRepository;
import com.billings.middlewareservice.services.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemServiceImplementation implements ItemService {

    private final ItemRepository itemRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final ObjectMapper objectMapper;

    @Override
    public ItemResponseDto createItem(ItemRequestDto request) {
        Item item = new Item();

        // 1. Auto-generate the unique item code (e.g., ITM-00001)
        String generatedCode = generateNextItemCode();
        item.setItemCode(generatedCode);
        item.setName(request.name());
        item.setDescription(request.description());
        item.setUnitPrice(request.unitPrice());
        item.setTaxRate(request.taxRate());

        Item savedItem = itemRepository.save(item);
        eventPublisher.publishEvent(new AuditEvent(
                "ITEM",
                savedItem.getId(),
                "INSERT",
                savedItem.getCreatedBy(),
                null,
                savedItem
        ));
        return convertToResponseDto(savedItem);
    }

    @Override
    public ItemResponseDto getItemById(UUID id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + id));
        return convertToResponseDto(item);
    }

    @Override
    public List<ItemResponseDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    public ItemResponseDto updateItems(UUID id, ItemRequestDto request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + id));

        // 2. Capture old state for auditing (Safe map)
        ItemResponseDto oldDto = convertToResponseDto(item);
        java.util.Map<String, Object> oldStateSnapshot = objectMapper.convertValue(oldDto, java.util.Map.class);

        // Update fields (We preserve item.getItemCode() since it is auto-generated and immutable!)
        item.setName(request.name());
        item.setDescription(request.description());
        item.setUnitPrice(request.unitPrice());
        item.setTaxRate(request.taxRate());

        Item updatedItem = itemRepository.save(item);

        eventPublisher.publishEvent(new AuditEvent(
                "ITEM",
                updatedItem.getId(),
                "UPDATE",
                updatedItem.getUpdatedBy(),
                oldStateSnapshot,
                updatedItem
        ));
        return convertToResponseDto(updatedItem);
    }

    @Override
    public void deleteItem(UUID id) {
        Item item = itemRepository.findById(id)
                .filter(Item::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Active item record not found for ID: " + id));

        // Perform Soft Delete
        item.setActive(false);
        item.setDeletedAt(java.time.Instant.now());

        Item deletedItem= itemRepository.save(item);

        // Fetch currently authenticated operator ID context
        String performanceOperatorId = deletedItem.getUpdatedBy();


        // Fire off async tracking event: Action='DELETE' into central ledger
        eventPublisher.publishEvent(new AuditEvent(
                "ITEM",
                deletedItem.getId(),
                "DELETE",
                performanceOperatorId,
                java.util.Map.of("isActive", true),
                java.util.Map.of("isActive", false, "deletedAt", deletedItem.getDeletedAt())
        ));
    }

    private ItemResponseDto convertToResponseDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getItemCode(),
                item.getName(),
                item.getDescription(),
                item.getUnitPrice(),
                item.getTaxRate()
        );
    }

    private synchronized String generateNextItemCode() {
        return itemRepository.findLastItemCode()
                .map(lastCode -> {
                    try {
                        // Extract numeric portion: "ITM-00005" -> 5
                        String numericPart = lastCode.substring(4);
                        long nextNum = Long.parseLong(numericPart) + 1;
                        return String.format("ITM-%05d", nextNum); // Outputs ITM-00006
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        return "ITM-00001"; // Fallback if parsing fails
                    }
                })
                .orElse("ITM-00001"); // If table is empty, start at 1
    }
}
