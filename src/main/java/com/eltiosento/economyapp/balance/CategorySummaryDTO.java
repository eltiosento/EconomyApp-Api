package com.eltiosento.economyapp.balance;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySummaryDTO {

    private Long categoryId;
    private String categoryName;
    private BigDecimal totalExpense;
    private BigDecimal monthlyExpense;
    private BigDecimal yearlyExpense;
    private String icon;
    private String description;
    private BigDecimal goal;
    private BigDecimal goalTotalProgress;
    private BigDecimal goalMonthlyProgress;
    private BigDecimal goalYearlyProgress;
    private boolean isSaving;
    private List<CategorySummaryDTO> subcategories;

}
