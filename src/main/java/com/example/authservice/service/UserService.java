package com.example.authservice.service;

import com.example.authservice.dto.RegisterRequest;

public interface UserService {

    String register(RegisterRequest request);

}