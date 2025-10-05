package com.example.oil_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpensesLogResponseDTO {
    private Long id;
    private Long expenseId;
    private String expenseName;
    private LocalDate date;
    private BigDecimal amount;
}