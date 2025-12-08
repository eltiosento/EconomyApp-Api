package com.eltiosento.economyapp.dto.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eltiosento.economyapp.dto.CategoryDTO;
import com.eltiosento.economyapp.model.Category;

@Component
public class CategoryDTOConverter {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Convert ExpenseCategoryDTO to ExpenseCategory
     * 
     * @param expenseCategory
     * @return ExpenseCategoryDTO
     */
    public CategoryDTO convertToDTO(Category expenseCategory) {

        return modelMapper.map(expenseCategory, CategoryDTO.class);
    }

}
