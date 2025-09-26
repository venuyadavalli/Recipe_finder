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
      this.recipeService.searchByName(this.searchTerm).subscribe(data => this.recipes = data);
    } else if (this.category) {
      this.recipeService.filterByCategory(this.category).subscribe(data => this.recipes = data);
    } else {
      this.recipeService.getAllRecipes().subscribe(data => this.recipes = data);
    }
  }

  viewDetails(recipe: Recipe) {
    this.router.navigate(['/recipes', recipe.id]);
  }

  onSearch(term: string) {
    this.searchTerm = term;
    this.loadRecipes();
  }
}
