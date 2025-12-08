package com.eltiosento.economyapp.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eltiosento.economyapp.model.Expense;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

        List<Expense> findAllByCategoryId(Long categoryId);

        @Query("SELECT e FROM Expense e JOIN e.category c WHERE c.parentCategory.id = :parentCategoryId")
        List<Expense> findAllByParentCategoryId(Long parentCategoryId);

        @Query("SELECT e FROM Expense e WHERE e.category.id =:categoryId AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
        List<Expense> findAllByCategoryIdMonth(Long categoryId, int month, int year);

        @Query("SELECT SUM(e.amount) FROM Expense e")
        Optional<BigDecimal> sumAllExpenses();

        @Query("SELECT SUM(e.amount) FROM Expense e WHERE MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
        Optional<BigDecimal> sumExpensesByMonth(int month, int year);

        @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category.id = :categoryId AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
        Optional<BigDecimal> sumExpensesByCategoryIdAndMonth(Long categoryId, int month, int year);

        @Query("SELECT SUM(e.amount) FROM Expense e JOIN e.category c WHERE c.parentCategory.id = :parentCategoryId AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
        Optional<BigDecimal> sumExpensesByParentCategoryAndMonth(Long parentCategoryId,
                        int month, int year);

        @Query("SELECT SUM(e.amount) FROM Expense e JOIN e.category c WHERE c.parentCategory.id = :parentCategoryId AND YEAR(e.expenseDate) = :year")
        Optional<BigDecimal> sumExpensesByParentCategoryAndYear(Long parentCategoryId,
                        int year);

        @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category.id = :categoryId AND YEAR(e.expenseDate) = :year")
        Optional<BigDecimal> sumExpensesByCategoryIdAndYear(Long categoryId, int year);

        @Query("SELECT SUM(e.amount) FROM Expense e JOIN e.category c WHERE c.parentCategory.id = :parentCategoryId")
        Optional<BigDecimal> sumExpensesByParentCategoryId(Long parentCategoryId);

        @Query("SELECT SUM(e.amount) FROM Expense e JOIN e.category c WHERE c.isSaving = true")
        Optional<BigDecimal> sumAllSavingsByTypeCategory();

        @Query("SELECT SUM(e.amount) FROM Expense e JOIN e.category c WHERE c.isSaving = true AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
        Optional<BigDecimal> sumAllBySavingsByTypeCategoryPerMonth(int month, int year);

        @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category.id = :categoryId")
        Optional<BigDecimal> sumExpensesByCategoryId(Long categoryId);
}
