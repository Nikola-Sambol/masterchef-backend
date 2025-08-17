package com.mev.recipeapp.projections;

import java.time.LocalDate;

public interface RecipeUserProjection {
    Long getId();
    String getRecipeName();
    LocalDate getCreationDate();
    String getImagePath();
    String getVideoPath();
    String getPreparationTime();
    String getUserName();
    String getUserSurname();
    String getUserEmail();
}
