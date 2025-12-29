# 🍳 Recipe Finder API

Base URL:
http://localhost:8080/api

---

## 📖 Recipe APIs

### 1. Get All Recipes
GET /recipes

---

### 2. Get Recipe by ID
GET /recipes/{id}

---

### 3. Create a Recipe (Manual)
POST /recipes

Request Body:
{
  "name": "Veg Curry",
  "category": "Vegetarian",
  "prepTime": 25,
  "ingredients": [
    { "name": "Potato", "quantity": "2" }
  ],
  "steps": "Cook vegetables with spices"
}

---

### 4. Create Single or Multiple Recipes (Manual Import)
POST /import/manual

Single Recipe:
{
  "name": "Sandwich",
  "category": "Snack"
}

Multiple Recipes:
[
  { "name": "Burger" },
  { "name": "Pizza" }
]

---

### 5. Update Recipe
PUT /recipes/{id}

Request Body:
{
  "name": "Updated Recipe",
  "prepTime": 30
}

---

### 6. Delete (Clear) a Single Recipe
DELETE /recipes/{id}/clear

---

### 7. Delete Multiple Recipes
POST /recipes/clear

Request Body:
[1, 2, 3]

---

## 🧂 Ingredient APIs

### 8. Get All Ingredients
GET /ingredients

---

## 🔍 Search & Filter APIs

### 9. Search Recipes by Name
GET /recipes/search?name=pasta

---

### 10. Filter Recipes by Category
GET /recipes/filter?category=Vegetarian

---

## 🧠 Recipe Suggestion API

### 11. Suggest Recipes by Ingredients
POST /recipes/suggest

Request Body:
{
  "ingredients": ["Tomato", "Onion"]
}

---

## 🌐 External Recipe Import APIs

Base URL:
/api/import

---

### 12. Bulk Import Recipes
POST /import/bulk?count=30

(Max limit: 200)

---

### 13. Import Vegetarian Recipes
POST /import/vegetarian?count=20

---

### 14. Import Recipe by Name
POST /import/{recipeName}

Example:
POST /import/Egg%20Sandwich

---

## 🗑 Clear Recipe APIs

### 15. Clear All Recipes
DELETE /import/clear

---

### 16. Clear Only API Imported Recipes
DELETE /import/clear/api

---

### 17. Clear Only Manually Added Recipes
DELETE /import/clear/manual
