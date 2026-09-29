package com.shopsphere.auth.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public Map<String, String> dashboard() {

        return Map.of(
                "message",
                "Welcome to the admin dashboard"
        );
    }
}
