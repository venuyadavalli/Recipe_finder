1. Get all recipes
GET /api/recipes


Returns a list of all recipes.

Response example:

[
  {
    "id": 1,
    "name": "Veg Sandwich",
    "category": "Vegetarian",
    "prepTime": 20,
    "imageUrl": "...",
    "steps": "..."
  }
]

2. Get a recipe by ID
GET /api/recipes/{id}


Example:

GET /api/recipes/1


Returns full details of a recipe by its ID.

3. Create a new recipe
POST /api/recipes


Request body: JSON of RecipeDTO

{
  "name": "Paneer Butter Masala",
  "category": "Vegetarian",
  "prepTime": 30,
  "imageUrl": "...",
  "steps": "Cook paneer..."
}


Response: Newly created recipe with id.

4. Update a recipe
PUT /api/recipes/{id}


Request body: JSON of RecipeDTO

Updates the recipe with the given ID.

5. Delete a recipe
DELETE /api/recipes/{id}


Deletes the recipe by ID.

6. List all ingredients
GET /api/ingredients


Returns all ingredients stored in your database.

7. Search recipes by name
GET /api/recipes/search?name={name}


Example:

GET /api/recipes/search?name=chicken


Returns all recipes containing the search term in the name.

8. Filter recipes by category
GET /api/recipes/filter?category={category}


Example:

GET /api/recipes/filter?category=Vegetarian
GET /api/recipes/filter?category=Non-Vegetarian


Returns recipes only in the specified category.

9. Suggest recipes by ingredients
POST /api/recipes/suggest


Request body:

{
  "ingredients": ["tomato", "paneer", "onion"]
}


📌 Import APIs

Bulk Import Recipes

POST http://localhost:8080/api/import/bulk?count=40


✅ Imports up to 40 recipes from TheMealDB.

Import by Recipe Name

POST http://localhost:8080/api/import/Egg%20Curry


✅ Imports a single recipe if found on TheMealDB.

Clear All Data

DELETE http://localhost:8080/api/import/clear