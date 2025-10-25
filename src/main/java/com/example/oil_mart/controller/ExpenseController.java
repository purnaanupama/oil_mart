package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.ExpensesLogRequestDTO;
import com.example.oil_mart.dto.request.ExpensesRequestDTO;
import com.example.oil_mart.dto.response.ExpensesLogResponseDTO;
import com.example.oil_mart.dto.response.ExpensesResponseDTO;
import com.example.oil_mart.service.ExpensesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpensesService expenseService;

    @PostMapping
    public ResponseEntity<ExpensesResponseDTO> createExpense(@Valid @RequestBody ExpensesRequestDTO requestDto) {
        ExpensesResponseDTO response = expenseService.createExpense(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/logs")
    public ResponseEntity<ExpensesLogResponseDTO> addExpenseLog(@Valid @RequestBody ExpensesLogRequestDTO requestDto) {
        ExpensesLogResponseDTO response = expenseService.addExpenseLog(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExpensesResponseDTO>> getAllExpenses() {
        List<ExpensesResponseDTO> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/logs/{id}")
    public ResponseEntity<Void> deleteExpenseLog(@PathVariable Long id) {
        expenseService.deleteExpenseLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logs")
    public ResponseEntity<List<ExpensesLogResponseDTO>> getAllExpenseLogs() {
        List<ExpensesLogResponseDTO> expenseLogs = expenseService.getAllExpenseLogs();
        return ResponseEntity.ok(expenseLogs);
    }
}