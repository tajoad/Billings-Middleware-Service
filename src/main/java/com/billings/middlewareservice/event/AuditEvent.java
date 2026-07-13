package com.billings.middlewareservice.event;

import java.util.UUID;

public record AuditEvent(
        String entityName,
        UUID entityId,
        String action,
        String performedBy,
        Object oldState,
        Object newState
) {
}
