package com.jarirahmed.springsecurity.controller;

import com.jarirahmed.springapi.dto.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Small endpoints that make authentication and method authorization observable. */
@RestController
@RequestMapping("/api/security")
public class SecurityStatusController {
    @GetMapping("/public")
    public ApiResponse<String> publicStatus() {
        return new ApiResponse<>("Public endpoint.", "No credentials were required.");
    }

    @GetMapping("/me")
    public ApiResponse<SecurityPrincipalResponse> currentPrincipal(Authentication authentication) {
        SecurityPrincipalResponse principal = new SecurityPrincipalResponse(
                authentication.getName(),
                authentication.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .sorted()
                        .toList(),
                authentication.isAuthenticated());
        return new ApiResponse<>("Authenticated principal.", principal);
    }

    /** The URL is authenticated in the filter chain; this annotation proves method security too. */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/method-admin")
    public ApiResponse<String> adminOnly() {
        return new ApiResponse<>("Admin authorization passed.", "Method security allowed this request.");
    }

    public record SecurityPrincipalResponse(
            String username,
            List<String> authorities,
            boolean authenticated) {
    }
}
