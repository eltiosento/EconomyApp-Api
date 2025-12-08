package com.eltiosento.economyapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeDTO {

    private Long id;
    private Long userId;
    private String userUsername;
    private String description;
    private BigDecimal amount;
    private LocalDate incomeDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
