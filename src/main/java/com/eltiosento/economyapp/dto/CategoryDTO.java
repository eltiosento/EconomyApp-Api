
package com.eltiosento.economyapp.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {

    private Long id;
    private String name;
    private String description;
    private String icon;
    private boolean isSaving; // Indica si la categoria és d'estalvi
    private BigDecimal goal; // Marcar objectiu d'estalvi o despesa per a la categoria
    private Long parentCategoryId;

}
