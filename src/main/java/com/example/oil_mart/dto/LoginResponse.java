package com.example.oil_mart.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Long user_id;
    private String username;
    private String status;
}
