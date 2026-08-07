package com.example.authservice.Controller;

import com.example.authservice.dto.ApiResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {

        String message = userService.register(request);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message(message)
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .build();

        ApiResponse<LoginResponse> response =
                ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Login Successful")
                        .data(loginResponse)
                        .build();

        return ResponseEntity.ok(response);
    }

}