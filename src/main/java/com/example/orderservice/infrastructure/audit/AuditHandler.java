package com.example.orderservice.infrastructure.audit;

import com.example.orderservice.domain.audit.Audit;

/**
 * The AuditService interface provides the contract for logging audit actions
 * within the coworking application. Implementations of this interface will handle
 * the persistence and management of audit records.
 */
public interface AuditHandler {

    /**
     * Logs an audit action by persisting the provided AuditDto.
     *
     * @param dto the Audit containing details of the action to be logged.
     */
    void logAction(Audit dto);
}
