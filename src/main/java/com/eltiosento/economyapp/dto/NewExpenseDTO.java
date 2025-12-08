package com.eltiosento.economyapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewExpenseDTO {

    private Long id;
    @NotNull(message = "User id is required")
    private Long userId;
    @NotNull(message = "Category id is required")
    private Long categoryId;
    private String description;
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

}
