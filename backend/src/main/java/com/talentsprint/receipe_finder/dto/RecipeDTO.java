package com.talentsprint.recipe_finder.dto;

import java.util.List;

public class RecipeDTO {
    private String name;
    private String category;
    private String description;
    private Integer prepTime;
    private String imageUrl;
    private String steps;
    private List<RecipeIngredientDTO> ingredients;

    public RecipeDTO() {}

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPrepTime() { return prepTime; }
    public void setPrepTime(Integer prepTime) { this.prepTime = prepTime; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public List<RecipeIngredientDTO> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredientDTO> ingredients) { this.ingredients = ingredients; }
}
