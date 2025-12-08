package com.eltiosento.economyapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eltiosento.economyapp.dto.ExpenseDTO;
import com.eltiosento.economyapp.dto.NewExpenseDTO;
import com.eltiosento.economyapp.dto.converter.ExpenseDTOConverter;
import com.eltiosento.economyapp.error.ExpenseBadRequestException;
import com.eltiosento.economyapp.error.ExpenseNotFoundException;
import com.eltiosento.economyapp.error.UserNotFoundException;
import com.eltiosento.economyapp.model.Category;
import com.eltiosento.economyapp.model.Expense;
import com.eltiosento.economyapp.model.User;
import com.eltiosento.economyapp.repository.CategoryRepository;
import com.eltiosento.economyapp.repository.ExpenseRepository;
import com.eltiosento.economyapp.repository.UserRepository;

@Service
public class ExpenseServiceImp implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseDTOConverter expenseDTOConverter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ExpenseDTO getExpenseById(Long id) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense with " + id + " not found"));

        return expenseDTOConverter.convertToDTO(expense);

    }

    @Override
    public List<ExpenseDTO> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();

        List<ExpenseDTO> expenseDTOs = new ArrayList<>();

        if (!expenses.isEmpty()) {

            expenseDTOs = expenses.stream()
                    .map(expense -> expenseDTOConverter.convertToDTO(expense))
                    .collect(Collectors.toList());
        }

        return expenseDTOs;
    }

    @Override
    public List<ExpenseDTO> getExpensesByCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ExpenseNotFoundException(
                        "Category with category id " + categoryId + " not exist."));

        List<Expense> expenses = expenseRepository.findAllByCategoryId(category.getId());

        List<ExpenseDTO> expenseDTOs = new ArrayList<>();

        if (!expenses.isEmpty()) {
            expenseDTOs = expenses.stream().map(expense -> expenseDTOConverter.convertToDTO(expense))
                    .collect(Collectors.toList());

        }

        return expenseDTOs;

    }

    @Override
    public List<ExpenseDTO> getExpensesByCategoryAndMonth(Long categoryId, int month, int year) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ExpenseNotFoundException(
                        "Category with category id " + categoryId + " not exist."));

        List<Expense> expenses = expenseRepository.findAllByCategoryIdMonth(category.getId(), month, year);

        List<ExpenseDTO> expenseDTOs = new ArrayList<>();

        if (!expenses.isEmpty()) {
            expenseDTOs = expenses.stream().map(expense -> expenseDTOConverter.convertToDTO(expense))
                    .collect(Collectors.toList());

        }

        return expenseDTOs;
    }

    @Override
    public List<ExpenseDTO> getExpensesByParentCategory(Long parentCategoryId) {

        Category category = categoryRepository.findById(
                parentCategoryId).orElseThrow(
                        () -> new ExpenseNotFoundException(
                                "Category with category id " + parentCategoryId + " not exist."));

        List<Expense> expenses = expenseRepository.findAllByParentCategoryId(category.getId());

        List<ExpenseDTO> expenseDTOs = new ArrayList<>();

        if (!expenses.isEmpty()) {
            expenseDTOs = expenses.stream().map(expense -> expenseDTOConverter.convertToDTO(expense))
                    .collect(Collectors.toList());

        }

        return expenseDTOs;
    }

    @Override
    public ExpenseDTO createExpense(NewExpenseDTO newExpenseDTO) {

        User user = userRepository.findById(newExpenseDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "User with user id " + newExpenseDTO.getUserId() + " not exist."));

        Category category = categoryRepository.findById(newExpenseDTO.getCategoryId()).orElseThrow(
                () -> new ExpenseNotFoundException(
                        "Category with category id " + newExpenseDTO.getCategoryId() + " not exist."));

        // Impedim que es creen despeses amb categories pare
        if (category.getParentCategory() == null) {
            throw new ExpenseBadRequestException("Category with category id " + newExpenseDTO.getCategoryId()
                    + " is a parent category. Please select a subcategory.");
        }

        Expense expense = expenseDTOConverter.convertToEntity(newExpenseDTO);
        Expense savedExpense = expenseRepository.save(expense);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setId(savedExpense.getId());
        expenseDTO.setAmount(savedExpense.getAmount());
        expenseDTO.setCategoryId(savedExpense.getCategory().getId());
        expenseDTO.setCategoryName(category.getName());
        expenseDTO.setDescription(savedExpense.getDescription());
        expenseDTO.setExpenseDate(savedExpense.getExpenseDate());
        expenseDTO.setUserId(savedExpense.getUser().getId());
        expenseDTO.setUserUsername(user.getUsername());
        expenseDTO.setCreatedAt(savedExpense.getCreatedAt());
        expenseDTO.setUpdatedAt(savedExpense.getUpdatedAt());

        return expenseDTO;

    }

    @Override
    public ExpenseDTO deleteExpense(Long id) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        ExpenseDTO expenseDTO = expenseDTOConverter.convertToDTO(expense);

        expenseRepository.delete(expense);

        return expenseDTO;

    }

    @Override
    public ExpenseDTO updateExpense(Long id, NewExpenseDTO newExpenseDTO) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        if (newExpenseDTO.getUserId() != null) {
            User user = userRepository.findById(newExpenseDTO.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(
                            "User with user id " + newExpenseDTO.getUserId() + " not exist."));
            expense.setUser(user);
        }

        if (newExpenseDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(newExpenseDTO.getCategoryId()).orElseThrow(
                    () -> new ExpenseNotFoundException(
                            "Category with category id " + newExpenseDTO.getCategoryId() + " not exist."));

            if (category.getParentCategory() == null) {
                throw new ExpenseBadRequestException("Category with category id " + newExpenseDTO.getCategoryId()
                        + " is a parent category. Please select a subcategory.");
            }

            expense.setCategory(category);
        }

        if (newExpenseDTO.getDescription() != null) {
            expense.setDescription(newExpenseDTO.getDescription());
        }
        if (newExpenseDTO.getAmount() != null) {
            expense.setAmount(newExpenseDTO.getAmount());
        }

        if (newExpenseDTO.getExpenseDate() != null) {
            expense.setExpenseDate(newExpenseDTO.getExpenseDate());
        }

        Expense updatedExpense = expenseRepository.save(expense);

        return expenseDTOConverter.convertToDTO(updatedExpense);

    }

}
