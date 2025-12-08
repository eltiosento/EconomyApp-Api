package com.eltiosento.economyapp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eltiosento.economyapp.dto.CategoryDTO;
import com.eltiosento.economyapp.dto.ParentCategoryDTO;
import com.eltiosento.economyapp.dto.converter.CategoryDTOConverter;
import com.eltiosento.economyapp.error.CategoryNotFoundException;
import com.eltiosento.economyapp.error.CategoryValidationException;
import com.eltiosento.economyapp.model.Category;
import com.eltiosento.economyapp.repository.CategoryRepository;

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryDTOConverter categoryDTOConverter;

    @Override
    public List<CategoryDTO> getAllCategories() {

        List<CategoryDTO> listCategoryDTOs = categoryRepository.findAll().stream()
                .map(category -> categoryDTOConverter.convertToDTO(category))
                .collect(Collectors.toList());

        if (listCategoryDTOs.isEmpty())
            throw new CategoryNotFoundException();

        return listCategoryDTOs;
    }

    @Override
    public List<CategoryDTO> getAllParentCategories() {

        List<CategoryDTO> listParentCategoriesDTOs = categoryRepository
                .findAllByParentCategoryIsNull().stream()
                .map(category -> categoryDTOConverter.convertToDTO(category))
                .collect(Collectors.toList());

        if (listParentCategoriesDTOs.isEmpty())
            throw new CategoryNotFoundException();

        return listParentCategoriesDTOs;

    }

    @Override
    public List<CategoryDTO> getAllSubCategories() {
        List<CategoryDTO> listSubCategoriesDTOs = categoryRepository
                .findAllByParentCategoryIsNotNull().stream()
                .map(category -> categoryDTOConverter.convertToDTO(category))
                .collect(Collectors.toList());

        if (listSubCategoriesDTOs.isEmpty())
            throw new CategoryNotFoundException();

        return listSubCategoriesDTOs;
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(
                        () -> new CategoryNotFoundException("Category whit id " + id + " not found"));

        return categoryDTOConverter.convertToDTO(category);
    }

    @Override
    public ParentCategoryDTO getParentCategoryById(Long id) {

        Category parentCategory = categoryRepository.findById(id)
                .orElseThrow(
                        () -> new CategoryNotFoundException("Category with id " + id + " not found"));

        ParentCategoryDTO parentCategoryDTO = new ParentCategoryDTO();
        parentCategoryDTO.setId(id);
        parentCategoryDTO.setName(parentCategory.getName());
        parentCategoryDTO.setDescription(parentCategory.getDescription());
        parentCategoryDTO.setIcon(parentCategory.getIcon());
        parentCategoryDTO.setSaving(parentCategory.isSaving());

        List<Category> subcategories = categoryRepository.findAllByParentCategoryId(id);

        List<CategoryDTO> subcategoriesDTO = new ArrayList<>();

        if (!subcategories.isEmpty()) {

            subcategoriesDTO = subcategories
                    .stream()
                    .map(subcategory -> categoryDTOConverter.convertToDTO(subcategory))
                    .collect(Collectors.toList());

        }

        parentCategoryDTO.setSubcategories(subcategoriesDTO);

        return parentCategoryDTO;
    }

    @Override
    public List<CategoryDTO> getAllSavingCategories() {

        List<CategoryDTO> listSavingCategoriesDTOs = categoryRepository
                .findAllByIsSavingTrue().stream()
                .map(category -> categoryDTOConverter.convertToDTO(category))
                .collect(Collectors.toList());

        if (listSavingCategoriesDTOs.isEmpty())
            throw new CategoryNotFoundException();

        return listSavingCategoriesDTOs;

    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {

        if (categoryDTO.getName() == null)
            throw new CategoryValidationException("The name is mandatory.");

        if (categoryDTO.getName().isBlank())
            throw new CategoryValidationException("The name cant be empty");

        if (categoryRepository.existsByName(categoryDTO.getName()))
            throw new CategoryValidationException(
                    "The name " + categoryDTO.getName() + " already exists.");

        Category newCategory = new Category();

        newCategory.setName(categoryDTO.getName());

        if (categoryDTO.getDescription() != null)
            newCategory.setDescription(categoryDTO.getDescription());

        if (categoryDTO.getIcon() != null)
            newCategory.setIcon(categoryDTO.getIcon());

        // Si ens passen el camp amb el valor true, la categoria és d'estalvi i ho
        // gaurdem així.
        // D'altra manera com que per defecte és false, no cal guardar-ho.
        // Aixi que no ens passen res es guardarà com a false.
        newCategory.setSaving(categoryDTO.isSaving());

        if (categoryDTO.getGoal() != null) {
            // Amb compareTo estem comparant valors BigDecimal aixo retorna un -1,0,1
            // Si el resultat és negatiu vol dir que el valor és menor que 1
            // Si el resultat és 0 vol dir que el valor és igual a 1
            // Si el resultat és positiu vol dir que el valor és major que 1
            // Pertnat al comprar-ho amb <0 estem rebutjant valors menors que 1.
            if (categoryDTO.getGoal().compareTo(BigDecimal.ONE) < 0) {
                newCategory.setGoal(null);
            } else {
                newCategory.setGoal(categoryDTO.getGoal());
            }
        }

        if (categoryDTO.getParentCategoryId() != null) {

            Category parentCategory = categoryRepository
                    .findById(categoryDTO.getParentCategoryId())
                    .orElseThrow(
                            () -> new CategoryNotFoundException(
                                    "Parent category with id " + categoryDTO.getParentCategoryId()
                                            + " not found"));

            newCategory.setParentCategory(parentCategory);
        }

        categoryRepository.save(newCategory);

        return categoryDTOConverter.convertToDTO(newCategory);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category with id " + id + " not found"));

        if (categoryDTO.getName() != null) {

            if (!categoryDTO.getName().equals(category.getName())
                    && categoryRepository.existsByName(categoryDTO.getName())) {
                throw new CategoryValidationException(
                        "The name " + categoryDTO.getName() + " already exists.");
            }

            if (!categoryDTO.getName().isBlank())
                category.setName(categoryDTO.getName().trim());

        }

        if (categoryDTO.getDescription() != null)
            category.setDescription(categoryDTO.getDescription().trim());

        if (categoryDTO.getIcon() != null)
            category.setIcon(categoryDTO.getIcon());

        category.setSaving(categoryDTO.isSaving());

        if (categoryDTO.getGoal() != null) {
            if (categoryDTO.getGoal().compareTo(BigDecimal.ONE) < 0) {
                category.setGoal(null);
            } else {
                category.setGoal(categoryDTO.getGoal());
            }
        }

        if (categoryDTO.getParentCategoryId() != null) {

            Category parentCategory = categoryRepository
                    .findById(categoryDTO.getParentCategoryId())
                    .orElseThrow(
                            () -> new CategoryNotFoundException(
                                    "Parent category with id " + categoryDTO.getParentCategoryId()
                                            + " not found"));

            category.setParentCategory(parentCategory);
        }

        categoryRepository.save(category);

        return categoryDTOConverter.convertToDTO(category);

    }

    @Override
    public CategoryDTO deleteCategory(Long id) {

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category with id " + id + " not found"));

        if (id <= 6)
            throw new CategoryValidationException("The category has a default category. You can't delete it.");

        CategoryDTO categoryDTO = categoryDTOConverter.convertToDTO(category);

        categoryRepository.delete(category);

        return categoryDTO;

    }

}
