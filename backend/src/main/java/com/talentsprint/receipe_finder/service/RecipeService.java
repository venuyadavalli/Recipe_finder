package com.talentsprint.recipe_finder.service;

import com.talentsprint.recipe_finder.model.*;
import com.talentsprint.recipe_finder.dto.*;
import com.talentsprint.recipe_finder.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    // List all recipes
    public List<RecipeResponseDTO> getAllRecipes() {
        return recipeRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Get recipe by ID
    public Optional<RecipeResponseDTO> getRecipeById(Long id) {
        return recipeRepository.findById(id).map(this::toResponse);
    }

    // Create recipe
    @Transactional
    public RecipeResponseDTO createRecipe(RecipeDTO dto) {
        Recipe recipe = new Recipe();
        recipe.setName(dto.getName());
        recipe.setCategory(dto.getCategory());
        recipe.setDescription(dto.getDescription());
        recipe.setPrepTime(dto.getPrepTime());
        recipe.setImageUrl(dto.getImageUrl());
        recipe.setSteps(dto.getSteps());
        recipe.setSource("MANUAL");

        Recipe saved = recipeRepository.save(recipe);

        if (dto.getIngredients() != null) {
            for (RecipeIngredientDTO ingDTO : dto.getIngredients()) {
                String ingName = ingDTO.getName().trim();

                Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(ingName)
                        .map(existing -> {
                            if (ingDTO.getImageUrl() != null) existing.setImageUrl(ingDTO.getImageUrl());
                            if (ingDTO.getType() != null) existing.setType(ingDTO.getType());
                            return ingredientRepository.save(existing);
                        })
                        .orElseGet(() -> {
                            Ingredient newIng = new Ingredient();
                            newIng.setName(ingName);
                            newIng.setImageUrl(ingDTO.getImageUrl());
                            newIng.setType(ingDTO.getType());
                            return ingredientRepository.save(newIng);
                        });

                RecipeIngredient ri = new RecipeIngredient();
                ri.setRecipe(saved);
                ri.setIngredient(ingredient);
                ri.setQuantity(ingDTO.getQuantity());
                ri.setId(new RecipeIngredientKey(saved.getId(), ingredient.getId()));
                saved.addRecipeIngredient(ri);
            }

            saved = recipeRepository.save(saved);
        }

        return toResponse(saved);
    }

    // Update recipe
    @Transactional
    public RecipeResponseDTO updateRecipe(Long id, RecipeDTO dto) {
        Recipe existing = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setDescription(dto.getDescription());
        existing.setPrepTime(dto.getPrepTime());
        existing.setImageUrl(dto.getImageUrl());
        existing.setSteps(dto.getSteps());

        if (dto.getIngredients() != null) {
            Map<Long, RecipeIngredient> existingIngredients = existing.getRecipeIngredients()
                    .stream().collect(Collectors.toMap(ri -> ri.getIngredient().getId(), ri -> ri));

            Set<Long> dtoIngredientIds = new HashSet<>();

            for (RecipeIngredientDTO ingDTO : dto.getIngredients()) {
                String ingName = ingDTO.getName().trim();

                Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(ingName)
                        .map(existingIng -> {
                            if (ingDTO.getImageUrl() != null) existingIng.setImageUrl(ingDTO.getImageUrl());
                            if (ingDTO.getType() != null) existingIng.setType(ingDTO.getType());
                            return ingredientRepository.save(existingIng);
                        })
                        .orElseGet(() -> {
                            Ingredient newIng = new Ingredient();
                            newIng.setName(ingName);
                            newIng.setImageUrl(ingDTO.getImageUrl());
                            newIng.setType(ingDTO.getType());
                            return ingredientRepository.save(newIng);
                        });

                dtoIngredientIds.add(ingredient.getId());

                if (existingIngredients.containsKey(ingredient.getId())) {
                    existingIngredients.get(ingredient.getId()).setQuantity(ingDTO.getQuantity());
                } else {
                    RecipeIngredient ri = new RecipeIngredient();
                    ri.setRecipe(existing);
                    ri.setIngredient(ingredient);
                    ri.setQuantity(ingDTO.getQuantity());
                    existing.getRecipeIngredients().add(ri);
                }
            }

            existing.getRecipeIngredients().removeIf(ri -> !dtoIngredientIds.contains(ri.getIngredient().getId()));
        }

        Recipe updated = recipeRepository.save(existing);
        return toResponse(updated);
    }

    // Delete recipe
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    // Search recipes
    public List<RecipeResponseDTO> searchByName(String name) {
        return recipeRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Filter by category
    public List<RecipeResponseDTO> filterByCategory(String category) {
        return recipeRepository.findByCategoryIgnoreCase(category)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // List all ingredients
    public List<Ingredient> listIngredients() {
        return ingredientRepository.findAll();
    }

    // Suggest recipes by ingredients
    public List<RecipeResponseDTO> suggestRecipes(List<String> userIngredients) {
        if (userIngredients == null) userIngredients = Collections.emptyList();
        Set<String> userSet = userIngredients.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<Recipe> all = recipeRepository.findAll();

        List<Recipe> matched = new ArrayList<>();
        for (Recipe r : all) {
            boolean allPresent = true;
            if (r.getRecipeIngredients() == null || r.getRecipeIngredients().isEmpty()) {
                allPresent = false;
            } else {
                for (RecipeIngredient ri : r.getRecipeIngredients()) {
                    String ingName = ri.getIngredient() != null ? ri.getIngredient().getName().toLowerCase() : "";
                    if (!userSet.contains(ingName)) {
                        allPresent = false;
                        break;
                    }
                }
            }
            if (allPresent) matched.add(r);
        }

        return matched.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Clear manual recipes
    @Transactional
    public void clearManualRecipes() {
        List<Recipe> manualRecipes = recipeRepository.findBySource("MANUAL");
        recipeRepository.deleteAll(manualRecipes);
    }

    // Clear API recipes
    @Transactional
    public void clearApiRecipes() {
        List<Recipe> apiRecipes = recipeRepository.findBySource("API");
        recipeRepository.deleteAll(apiRecipes);
    }

    // Map Recipe -> RecipeResponseDTO
    private RecipeResponseDTO toResponse(Recipe recipe) {
        RecipeResponseDTO resp = new RecipeResponseDTO();
        resp.setId(recipe.getId());
        resp.setName(recipe.getName());
        resp.setCategory(recipe.getCategory());
        resp.setDescription(recipe.getDescription());
        resp.setPrepTime(recipe.getPrepTime());
        resp.setImageUrl(recipe.getImageUrl());
        resp.setSteps(recipe.getSteps());

        List<RecipeIngredientDTO> ingList = recipe.getRecipeIngredients().stream()
                .map(ri -> {
                    RecipeIngredientDTO ridto = new RecipeIngredientDTO();
                    ridto.setName(ri.getIngredient() != null ? ri.getIngredient().getName() : null);
                    ridto.setQuantity(ri.getQuantity());
                    ridto.setImageUrl(ri.getIngredient() != null ? ri.getIngredient().getImageUrl() : null);
                    ridto.setType(ri.getIngredient() != null ? ri.getIngredient().getType() : null);
                    return ridto;
                }).collect(Collectors.toList());

        resp.setIngredients(ingList);
        return resp;
    }
}
