package com.jarirahmed.springtransactions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/** A small transactional record used to make commit and rollback observable. */
@Entity
@Table(name = "transaction_event")
public class TransactionEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String label;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TransactionEvent() {
        // JPA requires a no-argument constructor for entity materialization.
    }

    public TransactionEvent(String label) {
        this.label = label;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
