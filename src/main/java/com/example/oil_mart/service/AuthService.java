package com.example.oil_mart.service;

import com.example.oil_mart.dto.LoginRequest;
import com.example.oil_mart.dto.LoginResponse;
import com.example.oil_mart.dto.RegisterRequest;
import com.example.oil_mart.dto.RegisterResponse;
import com.example.oil_mart.model.User;
import com.example.oil_mart.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
   private final AuthRepository authRepository;
    public RegisterResponse createUser(RegisterRequest registerRequest) {
         User newUser = convertToUserEntity(registerRequest);
         newUser = authRepository.save(newUser);
         return convertToRegisterResponse(newUser);
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        User user = authRepository.findByUsername(loginRequest.getUsername());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        LoginResponse response = new LoginResponse();
        response.setUser_id(user.getUser_id());
        response.setUsername(user.getUsername());
        response.setStatus("Success");

        return response;
    }

    private User convertToUserEntity(RegisterRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }

    private RegisterResponse convertToRegisterResponse(User newUser) {
        return RegisterResponse.builder()
                .user_id(newUser.getUser_id())
                .username(newUser.getUsername())
                .build();
    }


}
