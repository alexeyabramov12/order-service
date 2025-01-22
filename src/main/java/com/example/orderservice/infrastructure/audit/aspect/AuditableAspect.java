package com.example.orderservice.infrastructure.audit.aspect;

import com.example.orderservice.domain.audit.Audit;
import com.example.orderservice.infrastructure.audit.AuditHandler;
import com.example.orderservice.infrastructure.audit.anatation.Auditable;
import com.example.orderservice.infrastructure.config.security.UserContextHelper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * This aspect intercepts methods annotated with the custom @Auditable annotation
 * and logs audit details including action, method signature, arguments, user, status, and errors.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditableAspect {

    private final AuditHandler auditHandler;
    private final UserContextHelper userContextHelper;

    /**
     * Pointcut that matches any method annotated with @Auditable.
     *
     * @param auditable the @Auditable annotation.
     */
    @Pointcut("@annotation(auditable)")
    public void auditableMethod(Auditable auditable) {
    }

    /**
     * Around advice that logs audit details for methods matched by the pointcut.
     * It captures the action, user, status, and any errors that occur during execution.
     *
     * @param joinPoint provides reflective access to the method being advised.
     * @param auditable the @Auditable annotation on the method.
     * @return the result of the method execution.
     * @throws Throwable if the method execution throws an exception.
     */
    @Around("auditableMethod(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object result;
        String error = null;
        String user = getCurrentUser();
        String status = "successful";
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            error = throwable.getMessage();
            status = "unsuccessful";
            throw throwable;
        } finally {
            Audit audit = new Audit(
                    user,
                    auditable.action(),
                    status,
                    LocalDateTime.now(),
                    error
            );
            audit.setIsDeleted(false);
            auditHandler.logAction(audit);
        }
        return result;
    }

    /**
     * Retrieves the current user from the HttpServletRequest.
     *
     * @return the current user, or "anonymous" if no user is found.
     */
    private String getCurrentUser() {
        return userContextHelper.getCurrentUserEmail();
    }
}
