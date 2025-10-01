package com.talentsprint.recipe_finder.controller;

import com.talentsprint.recipe_finder.repository.IngredientRepository;
import com.talentsprint.recipe_finder.repository.RecipeRepository;
import com.talentsprint.recipe_finder.service.RecipeImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/import")
@CrossOrigin(origins = "*")
public class ImportController {

    private final RecipeImportService importService;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public ImportController(RecipeImportService importService,
                            RecipeRepository recipeRepository,
                            IngredientRepository ingredientRepository) {
        this.importService = importService;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Bulk import up to `count` recipes (default 30).
     * Example: POST /api/import/bulk?count=40
     */
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> bulkImport(
            @RequestParam(name = "count", defaultValue = "30") int count) {
        if (count <= 0) count = 30;
        if (count > 200) count = 200; // safety cap
        Map<String, Object> result = importService.bulkImportFromTheMealDb(count);
        return ResponseEntity.ok(result);
    }

    /**
     * Import only vegetarian meals
     * Example: POST /api/import/vegetarian?count=20
     */
    @PostMapping("/vegetarian")
    public ResponseEntity<String> importVegetarian(@RequestParam(defaultValue = "20") int count) {
        importService.importVegetarianMeals(count);
        return ResponseEntity.ok("Imported " + count + " vegetarian meals successfully.");
    }

    /**
     * Clear all recipes + ingredients
     */
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAll() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
        return ResponseEntity.ok("All recipes and ingredients cleared!");
    }

    /**
     * Import one recipe by name (first match from TheMealDB).
     * Example: POST /api/import/Egg%20Sandwich
     */
    @PostMapping("/{name}")
    public ResponseEntity<String> importByName(@PathVariable String name) {
        boolean ok = importService.importByName(name);
        if (ok) return ResponseEntity.ok("Imported: " + name);
        else return ResponseEntity.badRequest().body("No recipe found for: " + name);
    }
}
