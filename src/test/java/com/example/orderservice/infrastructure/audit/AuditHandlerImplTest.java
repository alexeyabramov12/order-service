package com.example.orderservice.infrastructure.audit;

import com.example.orderservice.domain.audit.Audit;
import com.example.orderservice.infrastructure.repository.AuditRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование AuditHandlerImpl")
class AuditHandlerImplTest {

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditHandlerImpl auditHandler;

    @Test
    @DisplayName("Логирование действия через AuditRepository")
    void logAction_ShouldSaveAudit() {
        Audit audit = new Audit(
                "test_user",
                "CREATE",
                "SUCCESS",
                LocalDateTime.now(),
                null);
        audit.setIsDeleted(false);
        auditHandler.logAction(audit);

        verify(auditRepository, times(1)).save(audit);
    }
}
