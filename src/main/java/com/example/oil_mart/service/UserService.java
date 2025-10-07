package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.LoginRequestDTO;
import com.example.oil_mart.dto.request.UserRequestDTO;
import com.example.oil_mart.dto.response.LoginResponseDTO;
import com.example.oil_mart.dto.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);

    LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO);

}
