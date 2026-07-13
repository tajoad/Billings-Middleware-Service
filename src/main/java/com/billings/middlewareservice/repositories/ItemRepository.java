package com.billings.middlewareservice.repositories;

import com.billings.middlewareservice.entities.Customer;
import com.billings.middlewareservice.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    @Query("SELECT i.itemCode FROM Item i WHERE i.itemCode LIKE 'ITM-%' ORDER BY i.itemCode DESC LIMIT 1")
    Optional<String> findLastItemCode();
    boolean existsByItemCodeAndIdNot(String itemCode, UUID id);
}
