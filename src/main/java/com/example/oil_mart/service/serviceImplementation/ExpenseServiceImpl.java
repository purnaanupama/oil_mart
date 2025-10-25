package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.ExpensesLogRequestDTO;
import com.example.oil_mart.dto.request.ExpensesRequestDTO;
import com.example.oil_mart.dto.response.ExpensesLogResponseDTO;
import com.example.oil_mart.dto.response.ExpensesResponseDTO;
import com.example.oil_mart.model.Expenses;
import com.example.oil_mart.model.ExpensesLog;
import com.example.oil_mart.repository.ExpensesLogRepository;
import com.example.oil_mart.repository.ExpensesRepository;
import com.example.oil_mart.service.ExpensesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpensesService {

    private final ExpensesRepository expenseRepository;
    private final ExpensesLogRepository expenseLogRepository;

    @Override
    @Transactional
    public ExpensesLogResponseDTO addExpenseLog(ExpensesLogRequestDTO requestDto) {
        Expenses expense = expenseRepository.findById(requestDto.getExpenseId())
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + requestDto.getExpenseId()));

        ExpensesLog expenseLog = new ExpensesLog();
        expenseLog.setExpenses(expense);
        expenseLog.setDate(requestDto.getDate());
        expenseLog.setAmount(requestDto.getAmount());

        ExpensesLog savedExpenseLog = expenseLogRepository.save(expenseLog);

        return mapToExpenseLogResponseDto(savedExpenseLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpensesResponseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::mapToExpenseResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteExpenseLog(Long expenseLogId) {
        if (!expenseLogRepository.existsById(expenseLogId)) {
            throw new RuntimeException("Expense log not found with id: " + expenseLogId);
        }
        expenseLogRepository.deleteById(expenseLogId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpensesLogResponseDTO> getAllExpenseLogs() {
        return expenseLogRepository.findAll().stream()
                .map(this::mapToExpenseLogResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExpensesResponseDTO createExpense(ExpensesRequestDTO requestDto) {
        if (expenseRepository.existsByExpenseName(requestDto.getExpenseName())) {
            throw new RuntimeException("Expense with name '" + requestDto.getExpenseName() + "' already exists");
        }

        Expenses expense = new Expenses();
        expense.setExpenseName(requestDto.getExpenseName());

        Expenses savedExpense = expenseRepository.save(expense);

        return mapToExpenseResponseDto(savedExpense);
    }

    @Override
    @Transactional
    public void deleteExpense(Long expenseId) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new RuntimeException("Expense not found with id: " + expenseId);
        }

        if (expenseLogRepository.existsByExpensesId(expenseId)) {
            throw new RuntimeException("Cannot delete expense. This expense is being used in expense logs");
        }

        expenseRepository.deleteById(expenseId);
    }

    private ExpensesResponseDTO mapToExpenseResponseDto(Expenses expense) {
        return new ExpensesResponseDTO(
                expense.getId(),
                expense.getExpenseName()
        );
    }

    private ExpensesLogResponseDTO mapToExpenseLogResponseDto(ExpensesLog expenseLog) {
        return new ExpensesLogResponseDTO(
                expenseLog.getId(),
                expenseLog.getExpenses().getId(),
                expenseLog.getExpenses().getExpenseName(),
                expenseLog.getDate(),
                expenseLog.getAmount()
        );
    }
}