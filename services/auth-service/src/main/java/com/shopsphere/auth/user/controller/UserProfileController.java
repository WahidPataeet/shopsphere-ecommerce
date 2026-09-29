package com.shopsphere.auth.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/profile")
public class UserProfileController {

    @GetMapping
    public Map<String, Object> profile(
            Authentication authentication
    ) {

        return Map.of(
                "message", "Authenticated successfully",
                "email", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }
}
