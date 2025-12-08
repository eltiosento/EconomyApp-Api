package com.eltiosento.economyapp.service;

import java.util.List;

import com.eltiosento.economyapp.dto.ExpenseDTO;
import com.eltiosento.economyapp.dto.NewExpenseDTO;

public interface ExpenseService {

    ExpenseDTO getExpenseById(Long id);

    List<ExpenseDTO> getAllExpenses();

    List<ExpenseDTO> getExpensesByCategory(Long categoryId);

    List<ExpenseDTO> getExpensesByCategoryAndMonth(Long categoryId, int month, int year);

    List<ExpenseDTO> getExpensesByParentCategory(Long parentCategoryId);

    ExpenseDTO createExpense(NewExpenseDTO newExpenseDTO);

    ExpenseDTO deleteExpense(Long id);

    ExpenseDTO updateExpense(Long id, NewExpenseDTO newExpenseDTO);

}
