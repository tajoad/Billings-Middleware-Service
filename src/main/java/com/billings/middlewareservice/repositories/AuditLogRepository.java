package com.billings.middlewareservice.repositories;

import com.billings.middlewareservice.entities.AuditLog;
import com.billings.middlewareservice.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

}
