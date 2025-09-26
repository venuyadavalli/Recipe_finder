import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecipeService, Recipe } from '../../services/recipe.service';

@Component({
  selector: 'app-ingredient-suggestion',
  templateUrl: './ingredient-suggestion.component.html',
  styleUrls: ['./ingredient-suggestion.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]   // ✅ Add FormsModule here
})
export class IngredientSuggestionComponent {
  ingredientsInput: string = '';
  suggestedRecipes: Recipe[] = [];

  constructor(private recipeService: RecipeService) {}

  suggestRecipes() {
    const ingredients = this.ingredientsInput.split(',')
      .map(i => i.trim())
      .filter(i => i);

    if (ingredients.length > 0) {
      this.recipeService.suggestRecipes(ingredients)
        .subscribe(data => this.suggestedRecipes = data);
    }
  }
}
