package com.mev.recipeapp.repository;

import com.mev.recipeapp.models.Component;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponentRepository extends JpaRepository<Component, Long> {
    List<Component> findByRecipeId(Long recipeId);
}
