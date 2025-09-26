package com.talentsprint.recipe_finder.repository;

import com.talentsprint.recipe_finder.model.RecipeIngredient;
import com.talentsprint.recipe_finder.model.RecipeIngredientKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientKey> {
}
