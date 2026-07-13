package com.billings.middlewareservice.listener;

import com.billings.middlewareservice.entities.AuditLog;
import com.billings.middlewareservice.event.AuditEvent;
import com.billings.middlewareservice.repositories.AuditLogRepository;
import com.billings.middlewareservice.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {
    private final AuditLogRepository auditLogRepository;

    @Async
    @EventListener
    public void handleAuditEvent(AuditEvent event) {
        log.info("Processing async audit log event for {} operation on {}", event.action(), event.entityName());
            String oldValuesJson = event.oldState() != null ? JsonUtil.toJson(event.oldState()) : null;
            String newValuesJson = event.newState() != null ? JsonUtil.toJson(event.newState()) : null;

            AuditLog auditLog = AuditLog.builder()
                    .entityName(event.entityName())
                    .entityId(event.entityId())
                    .action(event.action())
                    .performedBy(event.performedBy())
                    .timestamp(Instant.now())
                    .oldValues(oldValuesJson)
                    .newValues(newValuesJson)
                    .build();

            auditLogRepository.save(auditLog);
    }
}
