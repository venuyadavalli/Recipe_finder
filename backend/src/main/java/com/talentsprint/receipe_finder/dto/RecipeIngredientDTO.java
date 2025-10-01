package com.talentsprint.recipe_finder.dto;

public class RecipeIngredientDTO {
    private String name;
    private String quantity;   // existing
    private String imageUrl;   // add this
    private String type;       // add this

    // getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}

