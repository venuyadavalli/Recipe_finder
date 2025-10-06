package com.talentsprint.recipe_finder.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String category;

    @Column(columnDefinition = "text")
    private String description;

    private Integer prepTime;

    @Column(length = 255)
    private String imageUrl;

    @ElementCollection(fetch = FetchType.EAGER) // force load to avoid LOB/auto-commit issues
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @OrderColumn(name = "step_index")              // preserves step order (0,1,2...)
    @Column(name = "step", columnDefinition = "TEXT")
    private List<String> steps = new ArrayList<>();




    @Column(length = 20)
    private String source; // "MANUAL" or "API"

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    public Recipe() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public List<RecipeIngredient> getRecipeIngredients() { return recipeIngredients; }
    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) { this.recipeIngredients = recipeIngredients; }

    public void addRecipeIngredient(RecipeIngredient ri) {
        recipeIngredients.add(ri);
        ri.setRecipe(this);
    }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
