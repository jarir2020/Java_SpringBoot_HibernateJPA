package com.jarirahmed.projects.storefront.repository;

import com.jarirahmed.projects.storefront.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProductRepository {
    @PersistenceContext
    private EntityManager entityManager;

    /** JPQL search joins the category and supports pagination at the database boundary. */
    public ProductPage search(String search, String category, int page, int size) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase();
        String normalizedCategory = category == null ? "" : category.trim().toLowerCase();
        String where = " from Product p join p.category c"
                + " where p.active = true"
                + " and (:search = '' or lower(p.name) like :pattern or lower(p.description) like :pattern)"
                + " and (:category = '' or lower(c.name) = :category)";

        long total = entityManager.createQuery("select count(p)" + where, Long.class)
                .setParameter("search", normalizedSearch)
                .setParameter("pattern", "%" + normalizedSearch + "%")
                .setParameter("category", normalizedCategory)
                .getSingleResult();

        TypedQuery<Product> query = entityManager.createQuery(
                "select p" + where + " order by p.id", Product.class);
        query.setParameter("search", normalizedSearch);
        query.setParameter("pattern", "%" + normalizedSearch + "%");
        query.setParameter("category", normalizedCategory);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return new ProductPage(query.getResultList(), total, page, size);
    }

    public Optional<Product> findById(long id) {
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        }
        return entityManager.merge(product);
    }
}
