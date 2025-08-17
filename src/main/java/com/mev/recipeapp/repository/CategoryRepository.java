package com.mev.recipeapp.repository;

import com.mev.recipeapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
