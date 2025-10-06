🍳 Recipe Finder API (Spring Boot + PostgreSQL)
1. List all recipes

GET http://localhost:8080/api/recipes

2. Get a recipe by ID

GET http://localhost:8080/api/recipes/{id}

3. Create a manual recipe

POST http://localhost:8080/api/recipes

4. Create multiple manual recipes

POST http://localhost:8080/api/recipes/bulk

5. Delete a single recipe

DELETE http://localhost:8080/api/recipes/{id}/clear

6. Delete multiple recipes

DELETE http://localhost:8080/api/recipes/clear

7. Import recipes from API (TheMealDB)

GET http://localhost:8080/api/import/bulk?count=10

8. Clear all API-imported recipes

DELETE http://localhost:8080/api/clear/api

9. Clear all manually added recipes

DELETE http://localhost:8080/api/clear/manual

10. Clear all recipes (API + Manual)

DELETE http://localhost:8080/api/clear

11. List all ingredients

GET http://localhost:8080/api/ingredients

12. Cleanup orphan ingredients

DELETE http://localhost:8080/api/ingredients/cleanup