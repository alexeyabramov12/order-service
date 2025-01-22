package com.example.orderservice.domain.audit;

import com.example.orderservice.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing an audit log.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit")
public class Audit extends BaseEntity {

    @Column(name = "user_login", nullable = false)
    private String userLogin;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "error", columnDefinition = "TEXT")
    private String error;
}
