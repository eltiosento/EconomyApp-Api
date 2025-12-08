package com.eltiosento.economyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eltiosento.economyapp.model.Category;

import jakarta.transaction.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByParentCategoryId(Long parentCategoryId);

    List<Category> findAllByParentCategoryIsNull();

    List<Category> findAllByParentCategoryIsNotNull();

    Boolean existsByName(String name);

    List<Category> findAllByIsSavingTrue();

}
