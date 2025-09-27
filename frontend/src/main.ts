import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

// ✅ Import your standalone components
import { HomePageComponent } from './app/pages/home/home.component';
import { RecipeListComponent } from './app/pages/recipe-list/recipe-list.component';
import { RecipeDetailComponent } from './app/pages/recipe-detail/recipe-detail.component';
import { IngredientSuggestionComponent } from './app/components/ingredient-suggestion/ingredient-suggestion.component';

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(HttpClientModule, FormsModule),
    provideRouter([
      { path: '', component: HomePageComponent },
      { path: 'recipes', component: RecipeListComponent },
      { path: 'recipes/:id', component: RecipeDetailComponent },
      { path: 'ingredients', component: IngredientSuggestionComponent } // ✅ Now it’s recognized
    ])
  ]
}).catch(err => console.error(err));
