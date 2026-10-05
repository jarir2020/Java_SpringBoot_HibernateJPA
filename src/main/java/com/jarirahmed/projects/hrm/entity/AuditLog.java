package com.jarirahmed.projects.hrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/** Audit rows record business changes without coupling the UI to entity internals. */
@Entity
@Table(name = "hrm_audit_log")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 80)
    private String actor;

    @Column(nullable = false, length = 40)
    private String action;

    @Column(nullable = false, length = 50)
    private String resourceType;

    @Column(nullable = false, length = 80)
    private String resourceId;

    @Column(nullable = false, length = 500)
    private String details;

    @Column(nullable = false)
    private Instant createdAt;

    protected AuditLog() {
    }

    public AuditLog(Company company, String actor, String action, String resourceType,
                    String resourceId, String details) {
        this.company = company;
        this.actor = actor;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.details = details;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getDetails() {
        return details;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
