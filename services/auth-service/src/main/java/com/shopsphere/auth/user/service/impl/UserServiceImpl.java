package com.shopsphere.auth.user.service.impl;

import com.shopsphere.auth.exception.DuplicateResourceException;
import com.shopsphere.auth.exception.ResourceNotFoundException;
import com.shopsphere.auth.role.RoleName;
import com.shopsphere.auth.role.entity.Role;
import com.shopsphere.auth.role.repository.RoleRepository;
import com.shopsphere.auth.user.dto.RegisterUserRequest;
import com.shopsphere.auth.user.dto.UserResponse;
import com.shopsphere.auth.user.entity.User;
import com.shopsphere.auth.user.repository.UserRepository;
import com.shopsphere.auth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse register(RegisterUserRequest request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "User already exists with email: " + email
            );
        }

        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() ->
                        new ResourceNotFoundException("CUSTOMER role not found")
                );

        User user = new User();

        user.setFirstName(request.firstName().trim());
        user.setLastName(
                request.lastName() == null
                        ? null
                        : request.lastName().trim()
        );
        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setStatus("ACTIVE");
        user.setRole(customerRole);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getStatus(),
                savedUser.getRole().getName().name()
        );
    }
}
