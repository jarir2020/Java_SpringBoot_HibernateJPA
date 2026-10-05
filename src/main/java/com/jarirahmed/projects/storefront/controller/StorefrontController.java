package com.jarirahmed.projects.storefront.controller;

import com.jarirahmed.projects.storefront.dto.AddToCartRequest;
import com.jarirahmed.projects.storefront.dto.CartResponse;
import com.jarirahmed.projects.storefront.dto.CategoryResponse;
import com.jarirahmed.projects.storefront.dto.CheckoutRequest;
import com.jarirahmed.projects.storefront.dto.OrderResponse;
import com.jarirahmed.projects.storefront.dto.PageResponse;
import com.jarirahmed.projects.storefront.dto.ProductResponse;
import com.jarirahmed.projects.storefront.dto.UserResponse;
import com.jarirahmed.projects.storefront.service.StorefrontService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST boundary used by the Project 4 storefront frontend. */
@RestController
@RequestMapping("/api/project4")
public class StorefrontController {
    private final StorefrontService service;

    public StorefrontController(StorefrontService service) {
        this.service = service;
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return service.categories();
    }

    @GetMapping("/products")
    public PageResponse<ProductResponse> products(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return service.products(search, category, page, size);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return service.currentUser(authentication.getName());
    }

    @GetMapping("/cart")
    public CartResponse cart(Authentication authentication) {
        return service.cart(authentication.getName());
    }

    @PostMapping("/cart/items")
    public CartResponse addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request) {
        return service.addToCart(authentication.getName(), request);
    }

    @DeleteMapping("/cart/items/{productId}")
    public CartResponse removeFromCart(
            Authentication authentication,
            @PathVariable long productId) {
        return service.removeFromCart(authentication.getName(), productId);
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            Authentication authentication,
            @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.checkout(authentication.getName(), request));
    }

    @GetMapping("/orders")
    public List<OrderResponse> orders(Authentication authentication) {
        return service.orders(authentication.getName());
    }
}
