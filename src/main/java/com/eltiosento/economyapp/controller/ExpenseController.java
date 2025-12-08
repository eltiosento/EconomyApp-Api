package com.eltiosento.economyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eltiosento.economyapp.dto.ExpenseDTO;
import com.eltiosento.economyapp.dto.NewExpenseDTO;
import com.eltiosento.economyapp.error.ApiError;
import com.eltiosento.economyapp.service.ExpenseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
@Tag(name = "Expense", description = "Controlador per a gestionar i consultar les despeses.")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseCategoryService;

    @Operation(summary = "Obtenim totes les despeses.", description = "Proveïm d'un mecanisme per a llistar totes les despes que anam generant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ha trobat les despeses.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ExpenseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "No ha trobat cap despesa.", content = @Content(schema = @Schema(implementation = ApiError.class))) })
    @GetMapping("/expenses")
    public ResponseEntity<?> getAllExpenses() {

        return ResponseEntity.ok(expenseCategoryService.getAllExpenses());
    }

    @Operation(summary = "Obtenim la informació d'una despesa.", description = "Proveïm d'un mecanisme per obtindre la informació d'una despesa en particular, passant com a paràmetre el seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ha trobat la despesa.", content = @Content(schema = @Schema(implementation = ExpenseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No ha trobat la despesa.", content = @Content(schema = @Schema(implementation = ApiError.class))) })
    @GetMapping("/expense/{id}")
    public ResponseEntity<?> getExpenseById(
            @Parameter(description = "ID de la despesa.", required = true) @PathVariable Long id) {

        return ResponseEntity.ok(expenseCategoryService.getExpenseById(id));
    }

    @GetMapping("/expenses/subcategory/{categoryId}")
    public ResponseEntity<?> getExpensesByCategory(@PathVariable Long categoryId) {

        return ResponseEntity.ok(expenseCategoryService.getExpensesByCategory(categoryId));
    }

    @GetMapping("/expenses/subcategory/{categoryId}/month/{month}/year/{year}")
    public ResponseEntity<?> getExpensesByCategoryAndMonth(
            @PathVariable Long categoryId,
            @PathVariable int month,
            @PathVariable int year) {

        return ResponseEntity.ok(expenseCategoryService.getExpensesByCategoryAndMonth(categoryId, month, year));
    }

    @GetMapping("/expenses/parent_category/{parentCategoryId}")
    public ResponseEntity<?> getExpensesByParentCategory(@PathVariable Long parentCategoryId) {

        return ResponseEntity.ok(expenseCategoryService.getExpensesByParentCategory(parentCategoryId));
    }

    @PostMapping("/expense")
    public ResponseEntity<?> createExpense(@Valid @RequestBody NewExpenseDTO newExpenseDTO) {

        ExpenseDTO expenseDTO = expenseCategoryService.createExpense(newExpenseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(expenseDTO);
    }

    @PutMapping("/expense/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody NewExpenseDTO newExpenseDTO) {

        ExpenseDTO expenseDTO = expenseCategoryService.updateExpense(id, newExpenseDTO);

        return ResponseEntity.ok(expenseDTO);
    }

    @DeleteMapping("/expense/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {

        return ResponseEntity.ok(expenseCategoryService.deleteExpense(id));
    }

}
