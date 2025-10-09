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
    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, RecipeIngredientRepository recipeIngredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    // ------------------- CRUD & Getters -------------------

    public List<RecipeResponseDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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

    // ✅ Fix: Keep same filename (don’t prepend /images/ or encode %20)
    // If user manually sets imageUrl, use it as-is; otherwise use default
    if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
        recipe.setImageUrl(dto.getImageUrl().trim());
    } else {
        recipe.setImageUrl("default-recipe.jpg");
    }

    recipe.setSteps(dto.getSteps());
    recipe.setSource("MANUAL");

    Recipe saved = recipeRepository.save(recipe);

    // ✅ Handle ingredients safely
    if (dto.getIngredients() != null) {
        for (RecipeIngredientDTO ingDTO : dto.getIngredients()) {
            String ingName = ingDTO.getName().trim();

            Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(ingName)
                    .map(existing -> {
                        if (ingDTO.getImageUrl() != null && !ingDTO.getImageUrl().isBlank()) {
                            // ✅ keep same file name, no path change
                            existing.setImageUrl(ingDTO.getImageUrl().trim());
                        } else if (existing.getImageUrl() == null || existing.getImageUrl().isBlank()) {
                            existing.setImageUrl("default-ingredient.jpg");
                        }

                        if (ingDTO.getType() != null) existing.setType(ingDTO.getType());
                        return ingredientRepository.save(existing);
                    })
                    .orElseGet(() -> {
                        Ingredient newIng = new Ingredient();
                        newIng.setName(ingName);
                        // ✅ again, no /images/ prefix — keep file name same
                        newIng.setImageUrl(
                                ingDTO.getImageUrl() != null && !ingDTO.getImageUrl().isBlank()
                                        ? ingDTO.getImageUrl().trim()
                                        : "default-ingredient.jpg"
                        );
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

public List<RecipeResponseDTO> createMultipleRecipes(List<RecipeDTO> recipeDTOList) {
    List<RecipeResponseDTO> createdRecipes = new ArrayList<>();

    for (RecipeDTO dto : recipeDTOList) {
        RecipeResponseDTO savedRecipe = createRecipe(dto); // this returns DTO already
        createdRecipes.add(savedRecipe);
    }

    return createdRecipes;
}


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

    // ------------------- CLEAR / DELETE -------------------

@Transactional
public void clearManualRecipes() {
    List<Recipe> manualRecipes = recipeRepository.findBySourceIgnoreCase("MANUAL");

    for (Recipe r : manualRecipes) {
        recipeIngredientRepository.deleteByRecipeId(r.getId());
    }

    recipeRepository.deleteAll(manualRecipes);
    cleanupOrphanIngredients();
}

@Transactional
public void clearApiRecipes() {
    List<Recipe> apiRecipes = recipeRepository.findBySourceIgnoreCase("API");

    for (Recipe r : apiRecipes) {
        recipeIngredientRepository.deleteByRecipeId(r.getId());
    }

    recipeRepository.deleteAll(apiRecipes);
    cleanupOrphanIngredients();
}

@Transactional
public void clearSingleRecipe(Long recipeId) {
    recipeIngredientRepository.deleteByRecipeId(recipeId);
    recipeRepository.deleteById(recipeId);
    cleanupOrphanIngredients();
}

@Transactional
public void clearRecipes(List<Long> recipeIds) {
    for (Long id : recipeIds) {
        recipeIngredientRepository.deleteByRecipeId(id);
    }
    recipeRepository.deleteAllById(recipeIds);
    cleanupOrphanIngredients();
}

@Transactional
protected void cleanupOrphanIngredients() {
    List<Ingredient> allIngredients = ingredientRepository.findAll();
    for (Ingredient ing : allIngredients) {
        if (ing.getRecipeIngredients() == null || ing.getRecipeIngredients().isEmpty()) {
            ingredientRepository.delete(ing);
        }
    }
}

@Transactional
public void clearAllRecipesSafely() {
    List<Recipe> allRecipes = recipeRepository.findAll();
    for (Recipe r : allRecipes) {
        recipeIngredientRepository.deleteByRecipeId(r.getId());
    }
    recipeRepository.deleteAll();
    cleanupOrphanIngredients();
}


    // ------------------- MAPPING -------------------

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
