package com.example.oil_mart.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpensesRequestDTO {

    @NotBlank(message = "Expense name is required")
    private String expenseName;
}