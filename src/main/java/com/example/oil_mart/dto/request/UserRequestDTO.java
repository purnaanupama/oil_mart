package com.example.oil_mart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank
    private String userName;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}

