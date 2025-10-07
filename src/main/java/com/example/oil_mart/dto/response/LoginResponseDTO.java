package com.example.oil_mart.dto.response;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String message;
    private Long userId;
    private String userName;
}
