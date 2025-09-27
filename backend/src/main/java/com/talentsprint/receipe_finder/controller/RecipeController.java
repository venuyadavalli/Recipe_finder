package com.talentsprint.recipe_finder.controller;

import com.talentsprint.recipe_finder.dto.*;
import com.talentsprint.recipe_finder.model.Ingredient;
import com.talentsprint.recipe_finder.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // List all recipes
    @GetMapping("/recipes")
    public List<RecipeResponseDTO> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    // Get recipe by id
    @GetMapping("/recipes/{id}")
    public ResponseEntity<RecipeResponseDTO> getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create recipe
    @PostMapping("/recipes")
    public ResponseEntity<RecipeResponseDTO> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        RecipeResponseDTO created = recipeService.createRecipe(recipeDTO);
        return ResponseEntity.created(URI.create("/api/recipes/" + created.getId())).body(created);
    }

    // Update recipe
    @PutMapping("/recipes/{id}")
    public ResponseEntity<RecipeResponseDTO> updateRecipe(
            @PathVariable Long id,
            @RequestBody RecipeDTO dto) {
        RecipeResponseDTO updated = recipeService.updateRecipe(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Delete recipe
    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    // List all ingredients
    @GetMapping("/ingredients")
    public List<Ingredient> listIngredients() {
        return recipeService.listIngredients();
    }

    // Search recipes by name
    @GetMapping("/recipes/search")
    public List<RecipeResponseDTO> searchByName(@RequestParam("name") String name) {
        return recipeService.searchByName(name);
    }

    // Filter recipes by category
    @GetMapping("/recipes/filter")
    public List<RecipeResponseDTO> filterByCategory(@RequestParam("category") String category) {
        return recipeService.filterByCategory(category);
    }

    // Suggest recipes by ingredients
    @PostMapping("/recipes/suggest")
    public List<RecipeResponseDTO> suggestRecipes(@RequestBody Map<String, List<String>> body) {
        List<String> ingredients = body.get("ingredients");
        return recipeService.suggestRecipes(ingredients);
    }
}
