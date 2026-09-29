package com.shopsphere.auth.auth.service.impl;

import com.shopsphere.auth.auth.dto.LoginRequest;
import com.shopsphere.auth.auth.dto.LoginResponse;
import com.shopsphere.auth.auth.service.AuthService;
import com.shopsphere.auth.security.JwtService;
import com.shopsphere.auth.user.entity.User;
import com.shopsphere.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpiration(),
                user.getId(),
                user.getEmail(),
                user.getRole().getName().name()
        );
    }
}
