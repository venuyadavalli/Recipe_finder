package com.talentsprint.recipe_finder.repository;

import com.talentsprint.recipe_finder.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByNameContainingIgnoreCase(String name);
    List<Recipe> findByCategoryIgnoreCase(String category);
}
