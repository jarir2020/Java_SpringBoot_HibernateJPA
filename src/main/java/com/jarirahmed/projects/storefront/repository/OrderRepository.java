package com.jarirahmed.projects.storefront.repository;

import com.jarirahmed.projects.storefront.entity.StoreOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public StoreOrder save(StoreOrder order) {
        if (order.getId() == null) {
            entityManager.persist(order);
            return order;
        }
        return entityManager.merge(order);
    }

    public List<StoreOrder> findByUserLogin(String login) {
        return entityManager.createQuery(
                        "select distinct o from StoreOrder o join fetch o.user u "
                                + "left join fetch o.items i "
                                + "where u.login = :login order by o.createdAt desc", StoreOrder.class)
                .setParameter("login", login)
                .getResultList();
    }
}
