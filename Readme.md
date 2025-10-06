📘 Recipe Finder API Documentation

Base URL:

http://localhost:8080/api

🧾 Recipe Endpoints
1️⃣ Get all recipes
GET /api/recipes


Description: Returns a list of all recipes (API + Manual).

2️⃣ Get recipe by ID
GET /api/recipes/{id}


Description: Retrieve a single recipe (including ingredients and steps).

3️⃣ Create a single manual recipe
POST /api/recipes
Content-Type: application/json


Body Example (Josh Format):

{
  "name": "Idli",
  "category": "Vegetarian",
  "description": "Soft steamed rice cakes made from fermented rice and lentil batter.",
  "prepTime": 25,
  "imageUrl": "idli.jpg",
  "steps": [
    "Soak rice and urad dal separately for 4-6 hours.",
    "Grind them into a smooth batter.",
    "Ferment overnight.",
    "Steam in idli molds for 10–15 minutes."
  ],
  "ingredients": [
    { "name": "Rice", "quantity": "2 cups", "type": "Grain" },
    { "name": "Urad Dal", "quantity": "1 cup", "type": "Pulse" },
    { "name": "Salt", "quantity": "to taste", "type": "Spice" }
  ]
}

4️⃣ Create multiple manual recipes at once
POST /api/recipes/bulk
Content-Type: application/json


Body Example:

[
  {
    "name": "Dosa",
    "category": "Vegetarian",
    "description": "Crispy South Indian crepe.",
    "prepTime": 20,
    "imageUrl": "dosa.jpg",
    "steps": ["Soak ingredients", "Grind", "Ferment", "Cook on pan"],
    "ingredients": [
      { "name": "Rice", "quantity": "2 cups", "type": "Grain" },
      { "name": "Urad Dal", "quantity": "1 cup", "type": "Pulse" }
    ]
  },
  {
    "name": "Sambar",
    "category": "Vegetarian",
    "description": "Spicy lentil stew with vegetables.",
    "prepTime": 40,
    "imageUrl": "sambar.jpg",
    "steps": ["Boil dal", "Add veggies", "Add tamarind & masala", "Boil until thick"],
    "ingredients": [
      { "name": "Toor Dal", "quantity": "1 cup", "type": "Pulse" },
      { "name": "Tamarind", "quantity": "2 tbsp", "type": "Fruit" }
    ]
  }
]

5️⃣ Delete single recipe
DELETE /api/recipes/{id}/clear


Description: Deletes one recipe (and its steps + ingredients link).
If any ingredient becomes orphaned, it’s automatically cleaned up.

6️⃣ Delete multiple recipes
DELETE /api/recipes/clear
Content-Type: application/json


Body Example:

[1, 2, 3]


Description: Deletes multiple recipes by IDs safely.

🌐 API Import Endpoints
7️⃣ Import recipes from TheMealDB (API)
GET /api/import/bulk?count=10


Description: Imports count number of recipes from TheMealDB API (e.g., 10 recipes).

8️⃣ Clear all API-imported recipes
DELETE /api/import/clear/api


Description: Deletes only recipes where source='API', along with related ingredients and steps.

✋ Manual Import Endpoints
9️⃣ Clear all manually created recipes
DELETE /api/import/clear/manual


Description: Deletes only recipes where source='MANUAL'.

🧹 Global Clear Endpoints
🔟 Clear all recipes (API + Manual)
DELETE /api/clear


Description: Completely deletes all recipes, ingredients, and steps safely.

🧩 Ingredient Management
11️⃣ Get all ingredients
GET /api/ingredients

12️⃣ Delete orphan ingredients
DELETE /api/ingredients/cleanup


Description: Deletes all ingredients that are not used in any recipe.