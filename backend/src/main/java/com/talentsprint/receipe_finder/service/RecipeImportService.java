package com.talentsprint.recipe_finder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.talentsprint.recipe_finder.model.Ingredient;
import com.talentsprint.recipe_finder.model.Recipe;
import com.talentsprint.recipe_finder.model.RecipeIngredient;
import com.talentsprint.recipe_finder.repository.IngredientRepository;
import com.talentsprint.recipe_finder.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeImportService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RestTemplate restTemplate;

    public RecipeImportService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Bulk import recipes from TheMealDB (letters a-z) until count is reached.
     */
    public Map<String, Object> bulkImportFromTheMealDb(int count) {
        Map<String, Object> result = new HashMap<>();
        int imported = 0;

        Map<String, Ingredient> ingredientCache = new HashMap<>();
        Set<String> existingRecipes = new HashSet<>();
        recipeRepository.findAll().forEach(r -> existingRecipes.add(r.getName().toLowerCase()));

        for (char c = 'a'; c <= 'z' && imported < count; c++) {
            String url = "https://www.themealdb.com/api/json/v1/1/search.php?f=" + c;
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response == null || !response.has("meals") || response.get("meals").isNull()) continue;

            for (JsonNode meal : response.get("meals")) {
                if (imported >= count) break;

                String mealName = meal.path("strMeal").asText("").trim();
                if (mealName.isEmpty() || existingRecipes.contains(mealName.toLowerCase())) continue;

                Recipe recipe = importMealNode(meal, ingredientCache);
                recipeRepository.save(recipe);

                existingRecipes.add(mealName.toLowerCase());
                imported++;
            }
        }

        result.put("importedCount", imported);
        return result;
    }

    /**
     * Import a single recipe by name (first match from TheMealDB)
     */
    public boolean importByName(String name) {
        try {
            String url = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + name;
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response == null || !response.has("meals") || response.get("meals").isNull()) return false;

            JsonNode meal = response.get("meals").get(0);
            Recipe recipe = importMealNode(meal, new HashMap<>());
            recipeRepository.save(recipe);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Import a single meal JSON node into Recipe + RecipeIngredients
     */
    private Recipe importMealNode(JsonNode meal, Map<String, Ingredient> ingredientCache) {
        Recipe recipe = new Recipe();

        // Name
        recipe.setName(meal.path("strMeal").asText("").trim());

        // Normalize category → Vegetarian / Non-Vegetarian
        String rawCategory = meal.path("strCategory").asText("").toLowerCase();
        String category = (rawCategory.contains("vegetable") || rawCategory.contains("vegetarian"))
                ? "Vegetarian"
                : "Non-Vegetarian";
        recipe.setCategory(category);

        // Description (area + tags)
        String area = meal.path("strArea").asText("");
        String tags = meal.path("strTags").asText("");
        String description = (!tags.isEmpty()) ? area + " | Tags: " + tags : area;
        recipe.setDescription(description);

        // Random realistic prep time
        int prepTime = 10 * (1 + (int)(Math.random() * 6));
        recipe.setPrepTime(prepTime);

        // Recipe image
        recipe.setImageUrl(meal.path("strMealThumb").asText("").trim());

        // Instructions → convert to List<String> steps
        String instructions = meal.path("strInstructions").asText();
if (instructions == null || instructions.isEmpty()) {
    instructions = "Instructions not available.";
}

// Convert to List<String> by splitting lines
List<String> stepList = Arrays.stream(instructions.split("\\r?\\n"))
                              .map(String::trim)
                              .filter(s -> !s.isEmpty())
                              .toList();

recipe.setSteps(stepList);


        // Ingredients
        Set<String> addedIngredients = new HashSet<>();
        for (int i = 1; i <= 20; i++) {
            String ingredientName = meal.path("strIngredient" + i).asText().trim();
            String quantity = meal.path("strMeasure" + i).asText().trim();

            if (ingredientName == null || ingredientName.isEmpty() || addedIngredients.contains(ingredientName)) {
                continue;
            }
            addedIngredients.add(ingredientName);

            Ingredient ingredient = ingredientCache.getOrDefault(ingredientName.toLowerCase(), null);
            if (ingredient == null) {
                ingredient = ingredientRepository.findByNameIgnoreCase(ingredientName)
                        .orElseGet(() -> {
                            Ingredient newIng = new Ingredient();
                            newIng.setName(ingredientName);
                            newIng.setImageUrl("https://www.themealdb.com/images/ingredients/"
                                    + ingredientName.replace(" ", "%20") + "-Small.png");
                            return ingredientRepository.save(newIng);
                        });
                ingredientCache.put(ingredientName.toLowerCase(), ingredient);
            }

            RecipeIngredient ri = new RecipeIngredient(recipe, ingredient, quantity);
            recipe.addRecipeIngredient(ri);
        }

        return recipe;
    }

    /**
     * Import only Vegetarian meals (filter API)
     */
    public void importVegetarianMeals(int count) {
        String url = "https://www.themealdb.com/api/json/v1/1/filter.php?c=Vegetarian";
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode meals = response.getBody().path("meals");

        if (meals.isArray()) {
            int imported = 0;
            Map<String, Ingredient> ingredientCache = new HashMap<>();

            for (JsonNode meal : meals) {
                if (imported >= count) break;

                String mealId = meal.path("idMeal").asText();

                // fetch full details
                String detailUrl = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + mealId;
                ResponseEntity<JsonNode> detailResponse = restTemplate.getForEntity(detailUrl, JsonNode.class);
                JsonNode fullMeal = detailResponse.getBody().path("meals").get(0);

                if (fullMeal != null) {
                    Recipe recipe = importMealNode(fullMeal, ingredientCache);
                    recipeRepository.save(recipe);
                    imported++;
                }
            }
        }
    }
}
