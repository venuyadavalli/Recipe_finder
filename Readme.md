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


Recipe APIs
1. Get All Recipes

Endpoint: GET /api/recipes

Description: Fetch all recipes.

Response: List of RecipeResponseDTO.

2. Get Recipe by ID

Endpoint: GET /api/recipes/{id}

Path Variable: id → Recipe ID

Description: Fetch single recipe by ID.

Response: RecipeResponseDTO or 404 Not Found.

3. Create Recipe (Manual)

Endpoint: POST /api/recipes

Request Body: RecipeDTO
Example:

{
  "name": "Paneer Butter Masala",
  "category": "Vegetarian",
  "description": "Delicious Indian curry with paneer cubes.",
  "prepTime": 30,
  "imageUrl": "paneer.jpg",
  "steps": ["Step 1", "Step 2"],
  "ingredients": [
    {"name": "Paneer", "quantity": "200g", "imageUrl": "", "type": "Dairy"},
    {"name": "Butter", "quantity": "50g", "imageUrl": "", "type": "Dairy"}
  ]
}


Description: Adds a manually created recipe.

Response: RecipeResponseDTO (created)

4. Update Recipe

Endpoint: PUT /api/recipes/{id}

Path Variable: id → Recipe ID

Request Body: RecipeDTO (same format as create)

Description: Updates an existing recipe.

Response: Updated RecipeResponseDTO

5. Delete Recipe

Endpoint: DELETE /api/recipes/{id}

Path Variable: id → Recipe ID

Description: Deletes a recipe.

Response: 204 No Content

6. Search Recipes by Name

Endpoint: GET /api/recipes/search

Query Parameter: name → Partial or full recipe name

Example: /api/recipes/search?name=Paneer

Description: Returns recipes containing the search keyword.

Response: List of RecipeResponseDTO.

7. Filter Recipes by Category

Endpoint: GET /api/recipes/filter

Query Parameter: category → "Vegetarian" or "Non-Vegetarian"

Example: /api/recipes/filter?category=Vegetarian

Response: List of RecipeResponseDTO.

8. Suggest Recipes by Ingredients

Endpoint: POST /api/recipes/suggest

Request Body: JSON containing list of ingredient names

{
  "ingredients": ["Paneer", "Butter", "Tomato"]
}


Description: Returns recipes that can be made using all provided ingredients.

Response: List of RecipeResponseDTO.

9. Clear Manual Recipes

Endpoint: DELETE /api/clear/manual

Description: Deletes all recipes with source = MANUAL.

Response: Success message

"All manually added recipes cleared!"

10. Clear API Imported Recipes

Endpoint: DELETE /api/clear/api

Description: Deletes all recipes with source = API.

Response: Success message

"All imported recipes cleared!"

Ingredient APIs
11. List All Ingredients

Endpoint: GET /api/ingredients

Description: Fetch all ingredients in the database.

Response: List of Ingredient objects

[
  {"id": 1, "name": "Paneer", "type": "Dairy", "imageUrl": "paneer.jpg"},
  {"id": 2, "name": "Butter", "type": "Dairy", "imageUrl": "butter.jpg"}
]

Notes / Extras

All manually added recipes automatically have source = MANUAL.

All imported recipes from TheMealDB automatically have source = API.

Searching, filtering, and suggesting recipes all work based on the ingredients and category stored in your database.

The clear endpoints (/clear/manual & /clear/api) are useful for resetting your DB separately for manual/API data.