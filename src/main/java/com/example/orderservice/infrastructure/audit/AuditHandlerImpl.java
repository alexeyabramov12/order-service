package com.example.orderservice.infrastructure.audit;

import com.example.orderservice.domain.audit.Audit;
import com.example.orderservice.infrastructure.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the AuditService interface that provides functionality
 * for logging audit actions within the coworking application.
 */

@Service
@RequiredArgsConstructor
public class AuditHandlerImpl implements AuditHandler {

    private final AuditRepository auditRepository;

    /**
     * Logs an audit action by converting the provided AuditDto to an entity
     * and persisting it using the AuditRepository.
     *
     * @param audit the Audit containing details of the action to be logged.
     */
    @Override
    public void logAction(Audit audit) {
        auditRepository.save(audit);
    }
}
