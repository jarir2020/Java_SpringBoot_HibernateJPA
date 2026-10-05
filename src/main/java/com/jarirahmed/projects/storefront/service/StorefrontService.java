package com.jarirahmed.projects.storefront.service;

import com.jarirahmed.projects.storefront.dto.AddToCartRequest;
import com.jarirahmed.projects.storefront.dto.CartResponse;
import com.jarirahmed.projects.storefront.dto.CategoryResponse;
import com.jarirahmed.projects.storefront.dto.CheckoutRequest;
import com.jarirahmed.projects.storefront.dto.OrderResponse;
import com.jarirahmed.projects.storefront.dto.PageResponse;
import com.jarirahmed.projects.storefront.dto.ProductResponse;
import com.jarirahmed.projects.storefront.dto.UserResponse;
import com.jarirahmed.projects.storefront.entity.Cart;
import com.jarirahmed.projects.storefront.entity.Product;
import com.jarirahmed.projects.storefront.entity.StoreOrder;
import com.jarirahmed.projects.storefront.entity.StoreUser;
import com.jarirahmed.projects.storefront.error.EmptyCartException;
import com.jarirahmed.projects.storefront.error.StorefrontNotFoundException;
import com.jarirahmed.projects.storefront.repository.CartRepository;
import com.jarirahmed.projects.storefront.repository.CategoryRepository;
import com.jarirahmed.projects.storefront.repository.OrderRepository;
import com.jarirahmed.projects.storefront.repository.ProductPage;
import com.jarirahmed.projects.storefront.repository.ProductRepository;
import com.jarirahmed.projects.storefront.repository.StoreUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Business and transaction boundary for the Project 4 storefront. */
@Service
public class StorefrontService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final StoreUserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public StorefrontService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            StoreUserRepository userRepository,
            CartRepository cartRepository,
            OrderRepository orderRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> categories() {
        return categoryRepository.findAll().stream().map(CategoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> products(String search, String category, int page, int size) {
        if (page < 0 || size < 1 || size > 50) {
            throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 50.");
        }
        ProductPage result = productRepository.search(search, category, page, size);
        return new PageResponse<>(
                result.products().stream().map(ProductResponse::from).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages());
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(String login) {
        return UserResponse.from(findUser(login));
    }

    @Transactional(readOnly = true)
    public CartResponse cart(String login) {
        return CartResponse.from(findCart(login));
    }

    @Transactional
    public CartResponse addToCart(String login, AddToCartRequest request) {
        Cart cart = findCart(login);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new StorefrontNotFoundException("product", request.productId()));
        if (!product.isActive()) {
            throw new IllegalArgumentException("Product is not available.");
        }
        cart.addProduct(product, request.quantity());
        cartRepository.save(cart);
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse removeFromCart(String login, long productId) {
        Cart cart = findCart(login);
        if (!cart.removeProduct(productId)) {
            throw new StorefrontNotFoundException("cart item for product", productId);
        }
        return CartResponse.from(cart);
    }

    /** One transaction updates inventory, creates order/payment rows, and clears the cart. */
    @Transactional
    public OrderResponse checkout(String login, CheckoutRequest request) {
        Cart cart = findCart(login);
        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }
        for (var item : cart.getItems()) {
            item.getProduct().decreaseInventory(item.getQuantity());
        }

        StoreOrder order = new StoreOrder(cart.getUser(), cart.total());
        for (var item : cart.getItems()) {
            order.addItem(item.getProduct(), item.getQuantity());
        }
        com.jarirahmed.projects.storefront.entity.Payment payment =
                new com.jarirahmed.projects.storefront.entity.Payment(
                        order,
                        order.getTotal(),
                        "DEMO-" + System.nanoTime());
        order.attachPayment(payment);
        orderRepository.save(order);
        cart.clear();
        cartRepository.save(cart);
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> orders(String login) {
        return orderRepository.findByUserLogin(login).stream().map(OrderResponse::from).toList();
    }

    private StoreUser findUser(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new StorefrontNotFoundException("store user", login));
    }

    private Cart findCart(String login) {
        findUser(login);
        return cartRepository.findByUserLogin(login)
                .orElseThrow(() -> new StorefrontNotFoundException("cart for user", login));
    }
}
