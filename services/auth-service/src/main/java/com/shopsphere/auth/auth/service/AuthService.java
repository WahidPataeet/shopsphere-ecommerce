package com.shopsphere.auth.auth.service;

import com.shopsphere.auth.auth.dto.LoginRequest;
import com.shopsphere.auth.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
