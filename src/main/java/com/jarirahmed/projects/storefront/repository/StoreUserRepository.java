package com.jarirahmed.projects.storefront.repository;

import com.jarirahmed.projects.storefront.entity.StoreUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StoreUserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<StoreUser> findByLogin(String login) {
        return entityManager.createQuery(
                        "select u from StoreUser u where u.login = :login", StoreUser.class)
                .setParameter("login", login)
                .getResultStream()
                .findFirst();
    }

    public StoreUser save(StoreUser user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        }
        return entityManager.merge(user);
    }
}
