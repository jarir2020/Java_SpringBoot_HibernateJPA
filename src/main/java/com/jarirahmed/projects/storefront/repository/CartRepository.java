package com.jarirahmed.projects.storefront.repository;

import com.jarirahmed.projects.storefront.entity.Cart;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CartRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Cart> findByUserLogin(String login) {
        return entityManager.createQuery(
                        "select distinct c from Cart c join fetch c.user u "
                                + "left join fetch c.items i left join fetch i.product p "
                                + "where u.login = :login", Cart.class)
                .setParameter("login", login)
                .getResultStream()
                .findFirst();
    }

    public Cart save(Cart cart) {
        if (cart.getId() == null) {
            entityManager.persist(cart);
            return cart;
        }
        return entityManager.merge(cart);
    }
}
