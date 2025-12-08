package com.eltiosento.economyapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewIncomeDTO {

    private Long id;
    @NotNull(message = "User id is required")
    private Long userId;
    private String description;
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    @NotNull(message = "Income date is required")
    private LocalDate incomeDate;

}
