package com.mev.recipeapp.repository;

import com.mev.recipeapp.models.Ingredients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientsRepository extends JpaRepository<Ingredients, Long> {

}
