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

    public List<RecipeResponseDTO> getAllRecipes() {
        return recipeRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Optional<RecipeResponseDTO> getRecipeById(Long id) {
        return recipeRepository.findById(id).map(this::toResponse);
    }

    @Transactional
public RecipeResponseDTO createRecipe(RecipeDTO dto) {
    Recipe recipe = new Recipe();
    recipe.setName(dto.getName());
    recipe.setCategory(dto.getCategory());
    recipe.setDescription(dto.getDescription());
    recipe.setPrepTime(dto.getPrepTime());
    recipe.setImageUrl(dto.getImageUrl());
    recipe.setSteps(dto.getSteps());  // now List<String> works perfectly

    // Save recipe first to get generated id
    Recipe saved = recipeRepository.save(recipe);

    // Process ingredients
    if (dto.getIngredients() != null) {
        for (RecipeIngredientDTO ingDTO : dto.getIngredients()) {
            String ingName = ingDTO.getName().trim();

            // Find existing ingredient or create new
            Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(ingName)
                    .orElseGet(() -> {
                        Ingredient newIng = new Ingredient();
                        newIng.setName(ingName);
                        newIng.setImageUrl(ingDTO.getImageUrl());  // now works
                        newIng.setType(ingDTO.getType());           // now works
                        return ingredientRepository.save(newIng);
                    });

            // Map RecipeIngredient
            RecipeIngredient ri = new RecipeIngredient();
            ri.setRecipe(saved);
            ri.setIngredient(ingredient);
            ri.setQuantity(ingDTO.getQuantity());

            ri.setId(new RecipeIngredientKey(saved.getId(), ingredient.getId()));
            saved.addRecipeIngredient(ri);
        }

        // Save recipe again with ingredients
        saved = recipeRepository.save(saved);
    }

    return toResponse(saved);
}


    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    public List<RecipeResponseDTO> searchByName(String name) {
        return recipeRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RecipeResponseDTO> filterByCategory(String category) {
        return recipeRepository.findByCategoryIgnoreCase(category)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<Ingredient> listIngredients() {
        return ingredientRepository.findAll();
    }

    /**
     * Suggest recipes that can be prepared with given ingredient names.
     * Basic algorithm (works for modest dataset): fetch all recipes and keep only recipes
     * whose every ingredient name is contained in the user's list (case-insensitive).
     */
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

    // Mapping to DTO helper
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
                    return ridto;
                }).collect(Collectors.toList());

        resp.setIngredients(ingList);
        return resp;
    }
 
@Transactional
public RecipeResponseDTO updateRecipe(Long id, RecipeDTO dto) {
    Recipe existing = recipeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

    // Update basic fields
    existing.setName(dto.getName());
    existing.setCategory(dto.getCategory());
    existing.setDescription(dto.getDescription());
    existing.setPrepTime(dto.getPrepTime());
    existing.setImageUrl(dto.getImageUrl());
    existing.setSteps(dto.getSteps());

    // Update ingredients
    if (dto.getIngredients() != null) {
        // Map existing ingredients by their IDs for easy lookup
        Map<Long, RecipeIngredient> existingIngredients = existing.getRecipeIngredients()
                .stream()
                .collect(Collectors.toMap(ri -> ri.getIngredient().getId(), ri -> ri));

        // Keep track of ingredient IDs present in DTO
        Set<Long> dtoIngredientIds = new HashSet<>();

        for (RecipeIngredientDTO ingDTO : dto.getIngredients()) {
            String ingName = ingDTO.getName().trim();

            // Find existing ingredient or create new one
            Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(ingName)
                    .orElseGet(() -> {
                        Ingredient newIng = new Ingredient();
                         newIng.setImageUrl(ingDTO.getImageUrl());
                         newIng.setType(ingDTO.getType());
                        return ingredientRepository.save(newIng);
                    });

            dtoIngredientIds.add(ingredient.getId());

            if (existingIngredients.containsKey(ingredient.getId())) {
                // Update quantity if ingredient already exists
                existingIngredients.get(ingredient.getId()).setQuantity(ingDTO.getQuantity());
            } else {
                // Add new RecipeIngredient
                RecipeIngredient ri = new RecipeIngredient();
                ri.setRecipe(existing);
                ri.setIngredient(ingredient);
                ri.setQuantity(ingDTO.getQuantity());
                existing.getRecipeIngredients().add(ri);
            }
        }

        // Remove ingredients not present in DTO
        existing.getRecipeIngredients().removeIf(ri -> !dtoIngredientIds.contains(ri.getIngredient().getId()));
    }

    // Save updated recipe
    Recipe updated = recipeRepository.save(existing);
    return toResponse(updated);
}
    private Long id;

    private String name;
    private String type;

 
    private String imageUrl; // Add this field

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    
}
