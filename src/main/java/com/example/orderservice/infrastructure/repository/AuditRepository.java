package com.example.orderservice.infrastructure.repository;

import com.example.orderservice.domain.audit.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The AuditRepository interface provides the contract for data access operations
 * related to audit records. Implementations of this interface will handle
 * the persistence of audit entities.
 */
public interface AuditRepository extends JpaRepository<Audit, Long> {
}
