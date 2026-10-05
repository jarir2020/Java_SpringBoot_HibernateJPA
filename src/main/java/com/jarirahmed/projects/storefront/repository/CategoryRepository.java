package com.jarirahmed.projects.storefront.repository;

import com.jarirahmed.projects.storefront.entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Category> findAll() {
        return entityManager.createQuery(
                        "select c from Category c order by c.name", Category.class)
                .getResultList();
    }

    public Optional<Category> findByName(String name) {
        return entityManager.createQuery(
                        "select c from Category c where lower(c.name) = lower(:name)", Category.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    public Category save(Category category) {
        if (category.getId() == null) {
            entityManager.persist(category);
            return category;
        }
        return entityManager.merge(category);
    }
}
