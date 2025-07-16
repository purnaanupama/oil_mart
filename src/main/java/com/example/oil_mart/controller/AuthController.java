package com.example.oil_mart.controller;

import com.example.oil_mart.dto.LoginRequest;
import com.example.oil_mart.dto.LoginResponse;
import com.example.oil_mart.dto.RegisterRequest;
import com.example.oil_mart.dto.RegisterResponse;
import com.example.oil_mart.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
         RegisterResponse response = authService.createUser(registerRequest);
         return response;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest){
       LoginResponse response = authService.loginUser(loginRequest);
       return response;
    }
}
