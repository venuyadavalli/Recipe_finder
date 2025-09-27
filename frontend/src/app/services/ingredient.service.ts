import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Ingredient {
  id: number;
  name: string;
  imageUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class IngredientService {
  private apiUrl = 'http://localhost:8080/api/ingredients';

  constructor(private http: HttpClient) {}

  getAllIngredients(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(this.apiUrl);
  }
}
