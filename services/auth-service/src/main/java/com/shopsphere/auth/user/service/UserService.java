package com.shopsphere.auth.user.service;

import com.shopsphere.auth.user.dto.RegisterUserRequest;
import com.shopsphere.auth.user.dto.UserResponse;

public interface UserService {

    UserResponse register(RegisterUserRequest request);
}