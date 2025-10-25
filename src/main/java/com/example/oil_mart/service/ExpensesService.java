package com.example.oil_mart.service;
import com.example.oil_mart.dto.request.ExpensesLogRequestDTO;
import com.example.oil_mart.dto.request.ExpensesRequestDTO;
import com.example.oil_mart.dto.response.ExpensesLogResponseDTO;
import com.example.oil_mart.dto.response.ExpensesResponseDTO;

import java.util.List;

public interface ExpensesService {

    ExpensesLogResponseDTO addExpenseLog(ExpensesLogRequestDTO requestDto);

    List<ExpensesResponseDTO> getAllExpenses();

    void deleteExpenseLog(Long expenseLogId);

    List<ExpensesLogResponseDTO> getAllExpenseLogs();

    ExpensesResponseDTO createExpense(ExpensesRequestDTO requestDto);

    void deleteExpense(Long expenseId);
}