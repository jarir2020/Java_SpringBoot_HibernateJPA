package com.jarirahmed.projects.storefront.seed;

import com.jarirahmed.projects.storefront.entity.Cart;
import com.jarirahmed.projects.storefront.entity.Category;
import com.jarirahmed.projects.storefront.entity.Product;
import com.jarirahmed.projects.storefront.entity.StoreUser;
import com.jarirahmed.projects.storefront.repository.CartRepository;
import com.jarirahmed.projects.storefront.repository.CategoryRepository;
import com.jarirahmed.projects.storefront.repository.ProductRepository;
import com.jarirahmed.projects.storefront.repository.StoreUserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Seeds disposable H2 teaching data so the browser has a useful first screen. */
@Component
public class StorefrontSeedData {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final StoreUserRepository userRepository;
    private final CartRepository cartRepository;

    public StorefrontSeedData(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            StoreUserRepository userRepository,
            CartRepository cartRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public void seed() {
        if (!categoryRepository.findAll().isEmpty()) {
            return;
        }

        Category desk = categoryRepository.save(new Category("Desk & Study"));
        Category field = categoryRepository.save(new Category("Field Notes"));
        Category home = categoryRepository.save(new Category("Home Rituals"));

        saveProduct(desk, "DSK-101", "Focus Desk Lamp",
                "A warm, adjustable lamp for a calmer study session.", "49.00", 12);
        saveProduct(desk, "DSK-202", "Mechanical Keyboard",
                "A tactile keyboard for long-form writing and coding.", "89.00", 7);
        saveProduct(field, "FLD-303", "Field Notebook Set",
                "Three durable notebooks for sketches, plans, and observations.", "18.50", 24);
        saveProduct(field, "FLD-404", "Canvas Daypack",
                "A lightweight daypack with room for a laptop and notebook.", "74.00", 5);
        saveProduct(home, "HOM-505", "Ceramic Tea Set",
                "A simple handmade set for a thoughtful afternoon break.", "36.00", 3);

        StoreUser shopper = userRepository.save(
                new StoreUser("shopper", "shopper@example.com", "Demo Shopper"));
        Cart cart = new Cart(shopper);
        shopper.attachCart(cart);
        cartRepository.save(cart);
    }

    private void saveProduct(
            Category category,
            String sku,
            String name,
            String description,
            String price,
            int inventory) {
        Product product = new Product(
                sku,
                name,
                description,
                new BigDecimal(price),
                inventory,
                true);
        category.addProduct(product);
        productRepository.save(product);
    }
}
