import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './pages/home/home.component';
import { RecipeListComponent } from './pages/recipe-list/recipe-list.component';
import { RecipeDetailComponent } from './pages/recipe-detail/recipe-detail.component';
import { IngredientSuggestionComponent } from './components/ingredient-suggestion/ingredient-suggestion.component';

export const routes: Routes = [
  { path: '', component: HomePageComponent },
  { path: 'recipes', component: RecipeListComponent },
  { path: 'recipes/:id', component: RecipeDetailComponent },
  { path: 'ingredients', component: IngredientSuggestionComponent }  // ✅ new route
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
