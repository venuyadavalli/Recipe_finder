package com.talentsprint.recipe_finder.dto;

public class RecipeIngredientDTO {
    private String name;
    private String quantity;

    public RecipeIngredientDTO() {}
    public RecipeIngredientDTO(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
}
