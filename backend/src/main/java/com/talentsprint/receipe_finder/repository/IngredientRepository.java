package com.talentsprint.recipe_finder.repository;

import com.talentsprint.recipe_finder.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByNameIgnoreCase(String name);
    List<Ingredient> findByNameIn(List<String> names);
}
