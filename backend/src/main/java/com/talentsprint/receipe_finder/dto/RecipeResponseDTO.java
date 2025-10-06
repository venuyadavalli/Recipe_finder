package com.talentsprint.recipe_finder.dto;

import java.util.ArrayList;
import java.util.List;

public class RecipeResponseDTO {
    private Long id;
    private String name;
    private String category;
    private String description;
    private Integer prepTime;

    // ✅ Must not be null (this fixes manual insert imageUrl=null issue)
    private String imageUrl;

    // ✅ Make sure steps list always initialized
    private List<String> steps = new ArrayList<>();

    private List<RecipeIngredientDTO> ingredients = new ArrayList<>();

    public RecipeResponseDTO() {}

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrepTime() {
        return prepTime;
    }
    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }

    public String getImageUrl() {
        // --- Fix: never return null, always return filename if exists ---
        return imageUrl != null ? imageUrl : "";
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getSteps() {
        return steps;
    }
    public void setSteps(List<String> steps) {
        this.steps = (steps != null) ? steps : new ArrayList<>();
    }

    public List<RecipeIngredientDTO> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<RecipeIngredientDTO> ingredients) {
        this.ingredients = (ingredients != null) ? ingredients : new ArrayList<>();
    }
}
