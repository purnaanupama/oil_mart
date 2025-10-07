package com.example.oil_mart.service.serviceImplementation;


import com.example.oil_mart.dto.request.LoginRequestDTO;
import com.example.oil_mart.dto.request.UserRequestDTO;
import com.example.oil_mart.dto.response.LoginResponseDTO;
import com.example.oil_mart.dto.response.UserResponseDTO;
import com.example.oil_mart.model.User;
import com.example.oil_mart.repository.UserRepository;
import com.example.oil_mart.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO dto) {
        // Optional: Check if user already exists
        if (userRepository.findByUserName(dto.getUserName()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Create user entity
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setPassword(dto.getPassword()); // You can hash this if needed

        // Save to DB
        User savedUser = userRepository.save(user);

        // Convert to response DTO
        UserResponseDTO response = new UserResponseDTO();
        response.setId(savedUser.getId());
        response.setUserName(savedUser.getUserName());
        response.setCreatedAt(savedUser.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));

        return response;
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUserName(loginRequestDTO.getUserName())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!user.getPassword().equals(loginRequestDTO.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        LoginResponseDTO response = new LoginResponseDTO();
        response.setMessage("Login successful");
        response.setUserId(user.getId());
        response.setUserName(user.getUserName());
        return response;
    }
}
