import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

import { RecipeService, Recipe } from '../../services/recipe.service';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';

@Component({
  selector: 'app-recipe-list',
  standalone: true,
  imports: [CommonModule, SearchBarComponent],
  templateUrl: './recipe-list.component.html',
  styleUrls: ['./recipe-list.component.css']
})
export class RecipeListComponent implements OnInit {
  recipes: Recipe[] = [];
  searchTerm: string = '';
  category: string = '';

  constructor(
    private recipeService: RecipeService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.category = params['category'] || '';
      this.loadRecipes();
    });
  }

  loadRecipes(): void {
    if (this.searchTerm) {
      this.recipeService.searchByName(this.searchTerm).subscribe(data => this.processRecipes(data));
    } else if (this.category) {
      this.recipeService.filterByCategory(this.category).subscribe(data => this.processRecipes(data));
    } else {
      this.recipeService.getAllRecipes().subscribe(data => this.processRecipes(data));
    }
  }

  /** 🧩 Fix image URLs for both manual and API recipes */
  processRecipes(data: Recipe[]): void {
    this.recipes = data.map(recipe => ({
      ...recipe,
      imageUrl: this.getImageUrl(recipe.imageUrl)
    }));
  }

  /** 🧠 Generate correct image path based on your backend setup */
  getImageUrl(imageUrl: string): string {
    if (!imageUrl) return 'assets/images/placeholder.jpg'; // fallback placeholder

    // If already a full URL (from API imports)
    if (imageUrl.startsWith('http')) return imageUrl;

    // If image saved in static/images (Spring Boot)
    return `http://localhost:8080/images/${imageUrl}`;
  }

  viewDetails(recipe: Recipe) {
    this.router.navigate(['/recipes', recipe.id]);
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.loadRecipes();
  }
}
