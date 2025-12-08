package com.eltiosento.economyapp.balance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/summary")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @GetMapping("/balance/global")
    public ResponseEntity<BalanceDTO> getGlobalSummary() {
        return ResponseEntity.ok(balanceService.getGlobalSummary());
    }

    @GetMapping("/balance/monthly/month/{month}/year/{year}")
    public ResponseEntity<BalanceDTO> getMonthlyYSummary(
            @PathVariable @Min(value = 1) @Max(value = 12) int month,
            @PathVariable @Min(value = 2025) @Max(value = 2100) int year) {
        return ResponseEntity.ok(balanceService.getMonthlySummary(month, year));
    }

    @GetMapping("/categories/subcategories")
    public ResponseEntity<?> getAllParantCategorisAndSubcategoris() {
        return ResponseEntity.ok(balanceService.getAllExpensesAndSubcategories());
    }

    @GetMapping("/savings/subcategories")
    public ResponseEntity<?> getAllSavingsSubCategories() {
        return ResponseEntity.ok(balanceService.getAllSavingsCategories());
    }

    @GetMapping("/category/{parentId}/subcategories/")
    public ResponseEntity<?> getExpenseByCategoryId(@PathVariable Long parentId) {
        return ResponseEntity.ok(balanceService.getExpenseByParentIdAndSubcategories(parentId));
    }

    @GetMapping("/categories/subcategories/month/{month}/year/{year}")
    public ResponseEntity<?> getAllParantCategorisAndSubcategorisMounthly(
            @PathVariable @Min(value = 1) @Max(value = 12) int month,
            @PathVariable @Min(value = 2025) @Max(value = 2100) int year) {
        return ResponseEntity.ok(balanceService.getAllExpensesAndSubcategoriesMonthly(month, year));
    }

    @GetMapping("/sub-category/{categoryId}/monthly/month/{month}/year/{year}")
    public ResponseEntity<?> getExpenseByCategoryIdAndMounthly(
            @PathVariable Long categoryId,
            @PathVariable @Min(value = 1) @Max(value = 12) int month,
            @PathVariable @Min(value = 2025) @Max(value = 2100) int year) {
        return ResponseEntity
                .ok(balanceService.getExpenseBySubCategoryIdMonthly(categoryId, month, year));
    }

    @GetMapping("/category/{parentId}/subcategories/monthly/month/{month}/year/{year}")
    public ResponseEntity<?> getExpenseByParentCategoryIdAndMounthlyDesglosed(@PathVariable Long parentId,
            @PathVariable @Min(value = 1) @Max(value = 12) int month,
            @PathVariable @Min(value = 2025) @Max(value = 2100) int year) {
        return ResponseEntity
                .ok(balanceService.getExpenseByParentIdSubcategoriesMonthly(parentId, month, year));
    }

}
