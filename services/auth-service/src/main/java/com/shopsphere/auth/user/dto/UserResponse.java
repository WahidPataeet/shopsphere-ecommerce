package com.shopsphere.auth.user.dto;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String status,
        String role
) {
}