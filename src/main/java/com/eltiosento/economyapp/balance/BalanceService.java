package com.eltiosento.economyapp.balance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eltiosento.economyapp.error.CategoryNotFoundException;
import com.eltiosento.economyapp.model.Category;
import com.eltiosento.economyapp.repository.CategoryRepository;
import com.eltiosento.economyapp.repository.ExpenseRepository;
import com.eltiosento.economyapp.repository.IncomeRepository;

@Service
public class BalanceService {

        @Autowired
        private IncomeRepository incomeRepository;

        @Autowired
        private ExpenseRepository expenseRepository;

        @Autowired
        private CategoryRepository expenseCategoryRepository;

        public BalanceDTO getGlobalSummary() {

                BigDecimal totalIncome = incomeRepository.sumAllIncomes().orElse(BigDecimal.ZERO);
                BigDecimal expenses = expenseRepository.sumAllExpenses().orElse(BigDecimal.ZERO);
                BigDecimal totalSavings = expenseRepository.sumAllSavingsByTypeCategory()
                                .orElse(BigDecimal.ZERO);

                BigDecimal saldo = totalIncome.subtract(expenses);
                BigDecimal totalExpense = expenses.subtract(totalSavings);
                BigDecimal patrimony = saldo.add(totalSavings);

                // Compte amb l'ordre dels paràmetres!!
                return new BalanceDTO(saldo, totalIncome, totalExpense, totalSavings, patrimony);
        }

        public BalanceDTO getMonthlySummary(int month, int year) {

                BigDecimal monthlyIncome = incomeRepository.sumIncomesByMonth(month, year).orElse(BigDecimal.ZERO);
                BigDecimal monthlyExpense = expenseRepository.sumExpensesByMonth(month, year).orElse(BigDecimal.ZERO);
                BigDecimal monthlySavings = expenseRepository
                                .sumAllBySavingsByTypeCategoryPerMonth(month, year)
                                .orElse(BigDecimal.ZERO);

                BigDecimal totalExpense = monthlyExpense.subtract(monthlySavings);
                BigDecimal monthlyBalance = monthlyIncome.subtract(monthlyExpense);

                return new BalanceDTO(monthlyBalance, monthlyIncome, totalExpense, monthlySavings,
                                calculatedPatrimony()); // Compte amb l'ordre dels paràmetres!!
        }

        public List<CategorySummaryDTO> getAllSavingsCategories() {

                List<Category> savingsCategories = expenseCategoryRepository.findAllByIsSavingTrue();
                // Si no hi ha categories de tipus estalvi, llança una excepció
                if (savingsCategories.isEmpty()) {
                        throw new CategoryNotFoundException("No savings categories found.");
                }

                List<CategorySummaryDTO> savingsCategorySummaryDTO = new ArrayList<>();

                for (Category category : savingsCategories) {

                        // Obtenim el total d'estalvis per a cada categoria
                        BigDecimal totalSaving = expenseRepository.sumExpensesByCategoryId(category.getId())
                                        .orElse(BigDecimal.ZERO);

                        // Calculem el progrés cap a l'objectiu d'estalvi
                        BigDecimal goalProgress = category.getGoal() != null
                                        ? totalSaving.divide(category.getGoal(), 2, RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;

                        // Crea un CategorySummaryDTO per cada categoria d'estalvi i afegeix-lo a la
                        // llista.
                        CategorySummaryDTO summaryDTO = new CategorySummaryDTO(
                                        category.getId(),
                                        category.getName(),
                                        totalSaving,
                                        BigDecimal.ZERO, // mensualitat no s'utilitza aquí
                                        BigDecimal.ZERO, // anualitat no s'utilitza aquí
                                        category.getIcon(),
                                        category.getDescription(),
                                        category.getGoal(),
                                        goalProgress,
                                        BigDecimal.ZERO, // mensualitat no s'utilitza aquí
                                        BigDecimal.ZERO, // anualitat no s'utilitza aquí
                                        category.isSaving(),
                                        null);
                        // Afegeix el DTO a la llista de resultats
                        savingsCategorySummaryDTO.add(summaryDTO);
                }

                return savingsCategorySummaryDTO;

        }

        public List<CategorySummaryDTO> getAllExpensesAndSubcategories() {

                List<Category> parentCategories = expenseCategoryRepository.findAllByParentCategoryIsNull();
                List<CategorySummaryDTO> allParentCategorySummaryDTO = new ArrayList<>();

                for (Category parentCategory : parentCategories) {

                        BigDecimal totalExpenseParentCategory = expenseRepository
                                        .sumExpensesByParentCategoryId(parentCategory.getId())
                                        .orElse(BigDecimal.ZERO);

                        BigDecimal goalProgressParentCategory = parentCategory.getGoal() != null
                                        ? totalExpenseParentCategory
                                                        .divide(parentCategory.getGoal(), 2, RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;

                        List<Category> subcategories = expenseCategoryRepository
                                        .findAllByParentCategoryId(parentCategory.getId());

                        CategorySummaryDTO parentSummaryDTO = new CategorySummaryDTO(
                                        parentCategory.getId(),
                                        parentCategory.getName(),
                                        totalExpenseParentCategory,
                                        BigDecimal.ZERO, // mensualitat no s'utilitza aquí
                                        BigDecimal.ZERO, // anualitat no s'utilitza aquí
                                        parentCategory.getIcon(),
                                        parentCategory.getDescription(),
                                        parentCategory.getGoal(),
                                        goalProgressParentCategory,
                                        BigDecimal.ZERO, // mensualitat no s'utilitza aquí
                                        BigDecimal.ZERO, // anualitat no s'utilitza aquí
                                        parentCategory.isSaving(),
                                        subcategories.stream().map(subcategory -> new CategorySummaryDTO(
                                                        subcategory.getId(),
                                                        subcategory.getName(),
                                                        expenseRepository.sumExpensesByCategoryId(subcategory.getId())
                                                                        .orElse(BigDecimal.ZERO),
                                                        BigDecimal.ZERO, // mensualitat no s'utilitza aquí
                                                        BigDecimal.ZERO, // anualitat no s'utilitza aquí
                                                        subcategory.getIcon(),
                                                        subcategory.getDescription(),
                                                        subcategory.getGoal(),
                                                        subcategory.getGoal() != null
                                                                        ? expenseRepository.sumExpensesByCategoryId(
                                                                                        subcategory.getId())
                                                                                        .orElse(BigDecimal.ZERO)
                                                                                        .divide(subcategory.getGoal(),
                                                                                                        2,
                                                                                                        RoundingMode.HALF_UP)
                                                                                        .multiply(BigDecimal
                                                                                                        .valueOf(100))
                                                                        : BigDecimal.ZERO,
                                                        BigDecimal.ZERO, // mensualitat no s'utilitza aquí
                                                        BigDecimal.ZERO, // anualitat no s'utilitza aquí
                                                        subcategory.isSaving(),
                                                        null))
                                                        .collect(Collectors.toList()));

                        allParentCategorySummaryDTO.add(parentSummaryDTO);

                }

                return allParentCategorySummaryDTO;
        }

        public CategorySummaryDTO getExpenseByParentIdAndSubcategories(Long parentCategoryId) {

                Category parentCategory = expenseCategoryRepository.findById(parentCategoryId)
                                .orElseThrow(
                                                () -> new CategoryNotFoundException(
                                                                "Category with id " + parentCategoryId
                                                                                + " not found."));

                BigDecimal totalExpenseParentCategory = expenseRepository
                                .sumExpensesByParentCategoryId(parentCategoryId)
                                .orElse(BigDecimal.ZERO);

                BigDecimal goalProgressParentCategory = parentCategory.getGoal() != null
                                ? totalExpenseParentCategory.divide(parentCategory.getGoal(), 2, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                List<Category> subcategories = expenseCategoryRepository
                                .findAllByParentCategoryId(parentCategoryId);

                CategorySummaryDTO parenSummaryDTO = new CategorySummaryDTO(
                                parentCategory.getId(),
                                parentCategory.getName(),
                                totalExpenseParentCategory,
                                BigDecimal.ZERO, // mensualitat no s'utilitza aquí
                                BigDecimal.ZERO, // anualitat no s'utilitza aquí
                                parentCategory.getIcon(),
                                parentCategory.getDescription(),
                                parentCategory.getGoal(),
                                goalProgressParentCategory,
                                BigDecimal.ZERO, // mensualitat no s'utilitza aquí
                                BigDecimal.ZERO, // anualitat no s'utilitza aquí
                                parentCategory.isSaving(),
                                subcategories.stream().map(subcategory -> new CategorySummaryDTO(
                                                subcategory.getId(),
                                                subcategory.getName(),
                                                expenseRepository.sumExpensesByCategoryId(subcategory.getId())
                                                                .orElse(BigDecimal.ZERO),
                                                BigDecimal.ZERO, // mensualitat no s'utilitza aquí
                                                BigDecimal.ZERO, // anualitat no s'utilitza aquí
                                                subcategory.getIcon(),
                                                subcategory.getDescription(),
                                                subcategory.getGoal(),
                                                subcategory.getGoal() != null
                                                                ? expenseRepository
                                                                                .sumExpensesByCategoryId(
                                                                                                subcategory.getId())
                                                                                .orElse(BigDecimal.ZERO)
                                                                                .divide(subcategory.getGoal(), 2,
                                                                                                RoundingMode.HALF_UP)
                                                                                .multiply(BigDecimal.valueOf(100))
                                                                : BigDecimal.ZERO,
                                                BigDecimal.ZERO, // mensualitat no s'utilitza aquí
                                                BigDecimal.ZERO, // anualitat no s'utilitza aquí
                                                subcategory.isSaving(),
                                                null))
                                                .collect(Collectors.toList()));

                return parenSummaryDTO;
        }

        /*------------------------------------------------------------------------- */
        public List<CategorySummaryDTO> getAllExpensesAndSubcategoriesMonthly(int month, int year) {

                List<Category> parentCategories = expenseCategoryRepository.findAllByParentCategoryIsNull();
                List<CategorySummaryDTO> allParentCategorySummaryDTO = new ArrayList<>();

                for (Category parentCategory : parentCategories) {

                        BigDecimal totalExpenseParentCategory = expenseRepository
                                        .sumExpensesByParentCategoryId(parentCategory.getId())
                                        .orElse(BigDecimal.ZERO);

                        BigDecimal monthlyExpenseParentCategory = expenseRepository
                                        .sumExpensesByParentCategoryAndMonth(parentCategory.getId(), month, year)
                                        .orElse(BigDecimal.ZERO);

                        BigDecimal yearlyExpenseParentCategory = expenseRepository
                                        .sumExpensesByParentCategoryAndYear(parentCategory.getId(), year)
                                        .orElse(BigDecimal.ZERO);

                        BigDecimal goalTotalProgressParentCategory = parentCategory.getGoal() != null
                                        ? totalExpenseParentCategory
                                                        .divide(parentCategory.getGoal(), 2, RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;

                        BigDecimal goalMonthlyProgressParentCategory = parentCategory.getGoal() != null
                                        ? monthlyExpenseParentCategory
                                                        .divide(parentCategory.getGoal(), 2, RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;

                        BigDecimal goalYearlyProgressParentCategory = parentCategory.getGoal() != null
                                        ? yearlyExpenseParentCategory
                                                        .divide(parentCategory.getGoal(), 2, RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;

                        List<Category> subcategories = expenseCategoryRepository
                                        .findAllByParentCategoryId(parentCategory.getId());

                        CategorySummaryDTO parenSummaryDTO = new CategorySummaryDTO(
                                        parentCategory.getId(),
                                        parentCategory.getName(),
                                        totalExpenseParentCategory,
                                        monthlyExpenseParentCategory,
                                        yearlyExpenseParentCategory,
                                        parentCategory.getIcon(),
                                        parentCategory.getDescription(),
                                        parentCategory.getGoal(),
                                        goalTotalProgressParentCategory,
                                        goalMonthlyProgressParentCategory,
                                        goalYearlyProgressParentCategory,
                                        parentCategory.isSaving(),
                                        subcategories.stream().map(subcategory -> new CategorySummaryDTO(
                                                        subcategory.getId(),
                                                        subcategory.getName(),
                                                        expenseRepository.sumExpensesByCategoryId(
                                                                        subcategory.getId())
                                                                        .orElse(BigDecimal.ZERO),
                                                        expenseRepository.sumExpensesByCategoryIdAndMonth(
                                                                        subcategory.getId(), month, year)
                                                                        .orElse(BigDecimal.ZERO),
                                                        expenseRepository.sumExpensesByCategoryIdAndYear(
                                                                        subcategory.getId(), year)
                                                                        .orElse(BigDecimal.ZERO),
                                                        subcategory.getIcon(),
                                                        subcategory.getDescription(),
                                                        subcategory.getGoal(),
                                                        subcategory.getGoal() != null
                                                                        ? expenseRepository.sumExpensesByCategoryId(
                                                                                        subcategory.getId())
                                                                                        .orElse(BigDecimal.ZERO)
                                                                                        .divide(subcategory.getGoal(),
                                                                                                        2,
                                                                                                        RoundingMode.HALF_UP)
                                                                                        .multiply(BigDecimal
                                                                                                        .valueOf(100))
                                                                        : BigDecimal.ZERO,
                                                        subcategory.getGoal() != null ? expenseRepository
                                                                        .sumExpensesByCategoryIdAndMonth(
                                                                                        subcategory.getId(), month,
                                                                                        year)
                                                                        .orElse(BigDecimal.ZERO)
                                                                        .divide(subcategory.getGoal(),
                                                                                        2,
                                                                                        RoundingMode.HALF_UP)
                                                                        .multiply(BigDecimal.valueOf(100))
                                                                        : BigDecimal.ZERO,
                                                        subcategory.getGoal() != null ? expenseRepository
                                                                        .sumExpensesByCategoryIdAndYear(
                                                                                        subcategory.getId(), year)
                                                                        .orElse(BigDecimal.ZERO)
                                                                        .divide(subcategory.getGoal(),
                                                                                        2,
                                                                                        RoundingMode.HALF_UP)
                                                                        .multiply(BigDecimal.valueOf(100))
                                                                        : BigDecimal.ZERO,
                                                        subcategory.isSaving(),
                                                        null))
                                                        .collect(Collectors.toList()));

                        allParentCategorySummaryDTO.add(parenSummaryDTO);

                }

                return allParentCategorySummaryDTO;
        }

        public CategorySummaryDTO getExpenseByParentIdSubcategoriesMonthly(Long parentCategoryId, int month,
                        int year) {

                Category category = expenseCategoryRepository.findById(parentCategoryId)
                                .orElseThrow(
                                                () -> new CategoryNotFoundException(
                                                                "Category with id " + parentCategoryId
                                                                                + " not found."));

                BigDecimal totalParentExpense = expenseRepository
                                .sumExpensesByParentCategoryId(parentCategoryId)
                                .orElse(BigDecimal.ZERO);

                BigDecimal monthlyExpenseParentCategory = expenseRepository
                                .sumExpensesByParentCategoryAndMonth(parentCategoryId, month, year)
                                .orElse(BigDecimal.ZERO);

                BigDecimal yearlyExpenseParentCategory = expenseRepository
                                .sumExpensesByParentCategoryAndYear(parentCategoryId, year)
                                .orElse(BigDecimal.ZERO);

                BigDecimal goalTotalProgressParentCategory = category.getGoal() != null
                                ? totalParentExpense.divide(category.getGoal(), 2, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                BigDecimal goalMonthlyProgressParentCategory = category.getGoal() != null
                                ? monthlyExpenseParentCategory.divide(category.getGoal(), 2, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                BigDecimal goalYearlyProgressParentCategory = category.getGoal() != null
                                ? yearlyExpenseParentCategory.divide(category.getGoal(), 2, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                List<Category> subcategories = expenseCategoryRepository
                                .findAllByParentCategoryId(parentCategoryId);

                List<CategorySummaryDTO> subcategoriesSummary = subcategories.stream()
                                .map(subcategory -> getExpenseBySubCategoryIdMonthly(subcategory.getId(), month, year))
                                .collect(Collectors.toList());

                return new CategorySummaryDTO(category.getId(), category.getName(),
                                totalParentExpense,
                                monthlyExpenseParentCategory,
                                yearlyExpenseParentCategory,
                                category.getIcon(),
                                category.getDescription(), category.getGoal(),
                                goalTotalProgressParentCategory,
                                goalMonthlyProgressParentCategory,
                                goalYearlyProgressParentCategory,
                                category.isSaving(),
                                subcategoriesSummary);
        }

        public CategorySummaryDTO getExpenseBySubCategoryIdMonthly(Long categoryId, int month, int year) {

                Category category = expenseCategoryRepository.findById(categoryId)
                                .orElseThrow(
                                                () -> new CategoryNotFoundException(
                                                                "Category with id " + categoryId + " not found."));

                BigDecimal totalExpense = expenseRepository
                                .sumExpensesByCategoryId(categoryId)
                                .orElse(BigDecimal.ZERO);

                BigDecimal monthlyExpense = expenseRepository
                                .sumExpensesByCategoryIdAndMonth(categoryId, month, year)
                                .orElse(BigDecimal.ZERO);

                BigDecimal yearlyExpense = expenseRepository
                                .sumExpensesByCategoryIdAndYear(categoryId, year)
                                .orElse(BigDecimal.ZERO);

                BigDecimal goalTotalProgress = category.getGoal() != null
                                ? totalExpense.divide(category.getGoal(), 2, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                BigDecimal goalMonthlyProgress = category.getGoal() != null
                                ? monthlyExpense.divide(category.getGoal(), 2, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                BigDecimal goalYearlyProgress = category.getGoal() != null
                                ? yearlyExpense.divide(category.getGoal(), 2, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                return new CategorySummaryDTO(category.getId(), category.getName(),
                                totalExpense,
                                monthlyExpense,
                                yearlyExpense,
                                category.getIcon(),
                                category.getDescription(), category.getGoal(),
                                goalTotalProgress,
                                goalMonthlyProgress,
                                goalYearlyProgress,
                                category.isSaving(), null);
        }

        private BigDecimal calculatedPatrimony() {

                BigDecimal totalIncome = incomeRepository.sumAllIncomes().orElse(BigDecimal.ZERO);
                BigDecimal expenses = expenseRepository.sumAllExpenses().orElse(BigDecimal.ZERO);
                BigDecimal totalSavings = expenseRepository.sumAllSavingsByTypeCategory()
                                .orElse(BigDecimal.ZERO);

                BigDecimal saldo = totalIncome.subtract(expenses);

                BigDecimal patrimony = saldo.add(totalSavings);
                return patrimony;
        }

}
