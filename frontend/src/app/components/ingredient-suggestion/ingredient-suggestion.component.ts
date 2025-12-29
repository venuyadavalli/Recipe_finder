import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ingredient-suggestion',
  templateUrl: './ingredient-suggestion.component.html',
  styleUrls: ['./ingredient-suggestion.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class IngredientSuggestionComponent implements OnInit {
  ingredients: any[] = [];
  filteredIngredients: any[] = [];
  searchTerm: string = '';
  selectedIngredients: string[] = [];
  suggestedRecipes: any[] = [];
  showSuggestions = false;


  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.http.get<any[]>('http://localhost:8080/api/ingredients')
      .subscribe(data => {
        this.ingredients = data;
        this.filteredIngredients = data;
      });
  }

  onSearch() {
    if (!this.searchTerm.trim()) {
      this.filteredIngredients = this.ingredients;
    } else {
      this.filteredIngredients = this.ingredients.filter(i =>
        i.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
  }

  toggleSelection(event: any) {
    const name = event.target.value;
    if (event.target.checked) {
      this.selectedIngredients.push(name);
    } else {
      this.selectedIngredients = this.selectedIngredients.filter(i => i !== name);
    }
  }
  toggleSelectionByName(name: string) {
  const index = this.selectedIngredients.indexOf(name);
  if (index > -1) {
    this.selectedIngredients.splice(index, 1);
  } else {
    this.selectedIngredients.push(name);
  }
  }


 suggestRecipes() {
  if (!this.selectedIngredients.length) return;
  this.http.post<any[]>('http://localhost:8080/api/recipes/suggest', {
    ingredients: this.selectedIngredients
  }).subscribe(data => {
    this.suggestedRecipes = data;
    this.showSuggestions = true; 
  });
  }

  goBackToSelection() {
  this.showSuggestions = false;
  this.suggestedRecipes = [];
  }



  viewDetails(recipe: any) {
    this.router.navigate(['/recipes', recipe.id]);
  }
}
