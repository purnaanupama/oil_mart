package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.LoginRequestDTO;
import com.example.oil_mart.dto.request.UserRequestDTO;
import com.example.oil_mart.dto.response.LoginResponseDTO;
import com.example.oil_mart.dto.response.UserResponseDTO;
import com.example.oil_mart.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserResponseDTO registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.registerUser(userRequestDTO);
    }

    @PostMapping("/login")
    public LoginResponseDTO loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return userService.loginUser(loginRequestDTO);
    }
}
