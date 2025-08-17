package com.mev.recipeapp.repository;

import com.mev.recipeapp.models.Recipe;
import com.mev.recipeapp.projections.RecipeUserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("""
       SELECT r.id AS id,
              r.recipeName AS recipeName,
              r.creationDate AS creationDate,
              r.image AS imagePath,
              r.video AS videoPath,
              r.preparationTime AS preparationTime,
              u.name AS userName,
              u.surname AS userSurname,
              u.email AS userEmail
       FROM Recipe r
       JOIN r.user u
       LEFT JOIN r.category c
       WHERE (:categoryId IS NULL OR c.id = :categoryId)
         AND (:recipeName IS NULL OR LOWER(r.recipeName) LIKE LOWER(CONCAT('%', :recipeName, '%')))
       ORDER BY r.id
       """)
    Page<RecipeUserProjection> findAllFiltered(
            @Param("categoryId") Long categoryId,
            @Param("recipeName") String recipeName,
            Pageable pageable
    );



    @Query("SELECT DISTINCT r FROM Recipe r " +
            "LEFT JOIN FETCH r.components c " +
            "LEFT JOIN FETCH c.ingredients i " +
            "LEFT JOIN FETCH c.instruction instr " +
            "LEFT JOIN FETCH r.category cat " +
            "LEFT JOIN FETCH r.user u " +
            "WHERE r.id = :recipeId")
    Optional<Recipe> findRecipeWithComponents(@Param("recipeId") Long recipeId);

    @Query("SELECT r FROM Recipe r " +
            "LEFT JOIN FETCH r.user u " +
            "LEFT JOIN FETCH u.role " +
            "ORDER BY r.id")
    List<Recipe> findAllWithUsers();


    Optional<List<Recipe>> findByUserId(Long userId);
}
