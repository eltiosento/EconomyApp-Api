package com.eltiosento.economyapp.service;

import java.util.List;

import com.eltiosento.economyapp.dto.CategoryDTO;
import com.eltiosento.economyapp.dto.ParentCategoryDTO;

public interface CategoryService {

    CategoryDTO getCategoryById(Long id);

    List<CategoryDTO> getAllCategories();

    List<CategoryDTO> getAllParentCategories();

    List<CategoryDTO> getAllSubCategories();

    List<CategoryDTO> getAllSavingCategories();

    ParentCategoryDTO getParentCategoryById(Long id);

    CategoryDTO createCategory(CategoryDTO expenseCategoryDTO);

    CategoryDTO deleteCategory(Long id);

    CategoryDTO updateCategory(Long id, CategoryDTO expenseCategoryDTO);

}
