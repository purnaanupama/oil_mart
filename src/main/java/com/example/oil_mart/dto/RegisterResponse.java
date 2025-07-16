package com.example.oil_mart.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private Long user_id;
    private String username;
}
