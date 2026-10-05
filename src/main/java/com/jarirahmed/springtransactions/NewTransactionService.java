package com.jarirahmed.springtransactions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Runs an independent transaction while an outer transaction is suspended. */
@Service
public class NewTransactionService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createEventThenFail(String label) {
        entityManager.persist(new TransactionEvent(label));
        throw new IllegalStateException("Inner transaction intentionally failed");
    }
}
