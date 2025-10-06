package com.talentsprint.recipe_finder.controller;

import com.talentsprint.recipe_finder.dto.*;
import com.talentsprint.recipe_finder.model.Ingredient;
import com.talentsprint.recipe_finder.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


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
    // Create bulk recipes
   @PostMapping("/import/manual")
    public ResponseEntity<?> createRecipeOrMultiple(@RequestBody Object body) {
    if (body instanceof List<?>) {
        // Handle multiple recipes
        List<?> list = (List<?>) body;
        List<RecipeDTO> recipeDTOList = new ArrayList<>();
        for (Object obj : list) {
            recipeDTOList.add(new ObjectMapper().convertValue(obj, RecipeDTO.class));
        }
        List<RecipeResponseDTO> created = recipeService.createMultipleRecipes(recipeDTOList);
        return ResponseEntity.ok(created);
    } else {
        // Handle single recipe
        RecipeDTO recipeDTO = new ObjectMapper().convertValue(body, RecipeDTO.class);
        RecipeResponseDTO created = recipeService.createRecipe(recipeDTO);
        return ResponseEntity.ok(created);
    }
}




    // Update recipe
    @PutMapping("/recipes/{id}")
    public ResponseEntity<RecipeResponseDTO> updateRecipe(
            @PathVariable Long id,
            @RequestBody RecipeDTO dto) {
        RecipeResponseDTO updated = recipeService.updateRecipe(id, dto);
        return ResponseEntity.ok(updated);
    }

    // // Delete recipe
    // @DeleteMapping("/recipes/{id}")
    // public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
    //     recipeService.deleteRecipe(id);
    //     return ResponseEntity.noContent().build();
    // }

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
  // DELETE single recipe
    @DeleteMapping("/recipes/{id}/clear")
    public ResponseEntity<String> clearSingleRecipe(@PathVariable Long id) {
        recipeService.clearSingleRecipe(id);
        return ResponseEntity.ok("Recipe with ID " + id + " cleared successfully!");
    }

    // DELETE multiple recipes by IDs (optional)
    @PostMapping("/recipes/clear")
    public ResponseEntity<String> clearRecipes(@RequestBody List<Long> ids) {
        recipeService.clearRecipes(ids);
        return ResponseEntity.ok("Selected recipes cleared successfully!");
    }
}
