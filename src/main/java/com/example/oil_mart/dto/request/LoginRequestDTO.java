package com.example.oil_mart.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    private String userName;

    @NotBlank
    private String password;
}
