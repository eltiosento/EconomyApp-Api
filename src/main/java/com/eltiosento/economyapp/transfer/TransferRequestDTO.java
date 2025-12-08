package com.eltiosento.economyapp.transfer;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    @NotNull(message = "User id is required")
    private Long userId;
    @NotNull(message = "From category id is required")
    private Long fromCategoryId;
    @NotNull(message = "To category id is required")
    private Long toCategoryId;
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    @NotNull(message = "Date is required")
    private LocalDate date;
    private String description;
}
