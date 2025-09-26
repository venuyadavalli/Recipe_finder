import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Ingredient {
  name: string;
  quantity: string;
}

export interface Recipe {
  id: number;
  name: string;
  category: string; // "veg" or "non-veg"
  description: string;
  prepTime: number;
  imageUrl: string;
  steps: string;
  ingredients: Ingredient[];
}

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  private apiUrl = 'http://localhost:8080/api/recipes';

  constructor(private http: HttpClient) { }

  getAllRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(this.apiUrl);
  }

  getRecipeById(id: number): Observable<Recipe> {
    return this.http.get<Recipe>(`${this.apiUrl}/${id}`);
  }

  filterByCategory(category: string): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(`${this.apiUrl}/filter?category=${category}`);
  }

  searchByName(name: string): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(`${this.apiUrl}/search?name=${name}`);
  }
  suggestRecipes(ingredients: string[]): Observable<Recipe[]> {
  return this.http.post<Recipe[]>(`${this.apiUrl}/suggest`, { ingredients });
}

}
