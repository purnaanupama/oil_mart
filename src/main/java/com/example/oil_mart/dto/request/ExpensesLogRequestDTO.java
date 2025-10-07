package com.example.oil_mart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpensesLogRequestDTO {
    private Long expenseId;
    private LocalDate date;
    private BigDecimal amount;
}