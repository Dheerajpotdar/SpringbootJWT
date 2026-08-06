package com.example.authservice.service;

import com.example.authservice.dto.LoginRequest;

public interface AuthService {

    String login(LoginRequest request);

}