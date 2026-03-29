package com.nwt.userservice.service;

import com.nwt.userservice.dto.request.LoginRequest;
import com.nwt.userservice.dto.request.RegisterRequest;
import com.nwt.userservice.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);
}
