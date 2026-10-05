package com.jarirahmed.projects.hrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

/** A queue row makes async notification work observable without an external mail service. */
@Entity
@Table(name = "hrm_notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 160)
    private String recipient;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, unique = true, length = 100)
    private String referenceKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant sentAt;

    protected Notification() {
    }

    public Notification(Company company, String recipient, String type, String message, String referenceKey) {
        this.company = company;
        this.recipient = recipient;
        this.type = type;
        this.message = message;
        this.referenceKey = referenceKey;
        this.status = NotificationStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void markSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getReferenceKey() {
        return referenceKey;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
