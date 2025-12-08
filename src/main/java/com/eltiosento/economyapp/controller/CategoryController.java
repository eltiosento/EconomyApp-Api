package com.eltiosento.economyapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eltiosento.economyapp.dto.CategoryDTO;
import com.eltiosento.economyapp.dto.ParentCategoryDTO;
import com.eltiosento.economyapp.service.CategoryService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryDTO);
    }

    @GetMapping("/category/{id}/subcategories")
    public ResponseEntity<?> getParentCategoryById(@PathVariable Long id) {

        ParentCategoryDTO parentCategoryDTO = categoryService.getParentCategoryById(id);
        return ResponseEntity.ok(parentCategoryDTO);
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {

        List<CategoryDTO> listAllCategoryDTOs = categoryService.getAllCategories();
        return ResponseEntity.ok(listAllCategoryDTOs);
    }

    @GetMapping("/parent_categories")
    public ResponseEntity<?> getAllParentCategories() {

        List<CategoryDTO> listAllCategoryDTOs = categoryService.getAllParentCategories();
        return ResponseEntity.ok(listAllCategoryDTOs);
    }

    @GetMapping("/sub_categories")
    public ResponseEntity<?> getAllSubCategories() {

        List<CategoryDTO> listAllCategoryDTOs = categoryService.getAllSubCategories();
        return ResponseEntity.ok(listAllCategoryDTOs);
    }

    @GetMapping("/saving_categories")
    public ResponseEntity<?> getAllSavingCategories() {

        List<CategoryDTO> listAllCategoryDTOs = categoryService.getAllSavingCategories();
        return ResponseEntity.ok(listAllCategoryDTOs);
    }

    @PostMapping("/category")
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO categoryDTO) {

        CategoryDTO newCategoryDTO = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategoryDTO);
    }

    @PutMapping("category/{id}")
    public ResponseEntity<?> createCategory(@PathVariable Long id,
            @RequestBody CategoryDTO categoryDTO) {

        CategoryDTO modifiCategoryDTO = categoryService.updateCategory(id,
                categoryDTO);

        return ResponseEntity.ok(modifiCategoryDTO);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {

        CategoryDTO categoryDTO = categoryService.deleteCategory(id);

        return ResponseEntity.ok(categoryDTO);

    }
}
