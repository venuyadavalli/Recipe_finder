# 🍳 Recipe Finder Application

Recipe Finder is a **full-stack web application** that helps users discover recipes, search and filter them, and get **recipe suggestions based on selected ingredients**.  
The project also supports **manual recipe management** and **bulk recipe import from external APIs**.

---

## 🚀 Features

- View all recipes
- View recipe details
- Search recipes by name
- Filter recipes by category
- Select ingredients and get suggested recipes
- Import recipes from an external API (TheMealDB)
- Add recipes manually
- Update and delete recipes safely
- Responsive UI (Desktop & Mobile)

---

## 🛠 Tech Stack

### Backend
- Java
- Spring Boot
- Spring REST APIs
- Spring Data JPA
- PostgreSQL

### Frontend
- Angular (Standalone Components)
- HTML
- CSS
- TypeScript

---

## 📂 Project Structure (High Level)

recipe-finder/
│
├── backend/
│ ├── controller
│ ├── service
│ ├── repository
│ ├── dto
│ └── model
│
├── frontend/
│ ├── components
│ ├── services
│ └── pages
│
└── README.md

yaml
Copy code

---

## 🌐 Backend API Overview

**Base URL**
http://localhost:8080/api

markdown
Copy code

### Recipe APIs
- Get all recipes
- Get recipe by ID
- Create recipe
- Update recipe
- Delete single or multiple recipes
- Search and filter recipes
- Suggest recipes based on ingredients

### Ingredient APIs
- List all ingredients

### Import APIs
- Bulk import recipes
- Import vegetarian recipes
- Import recipe by name
- Clear imported or manual recipes safely

---

## 🖥 Frontend Overview

- Home page with navigation
- Recipe list page
- Recipe detail page
- Ingredient suggestion page
- Search bar with responsive design
- Mobile menu (hamburger navigation)
- User-friendly ingredient selection with selected summary

---

## 🧪 How to Run the Project

### Backend (Spring Boot)

1. Configure PostgreSQL database
2. Update `application.properties`
3. Run:
mvn spring-boot:run

nginx
Copy code

Backend runs on:
http://localhost:8080

yaml
Copy code

---

### Frontend (Angular)

1. Navigate to frontend folder
2. Install dependencies:
npm install

markdown
Copy code
3. Start application:
ng serve

nginx
Copy code

Frontend runs on:
http://localhost:4200

yaml
Copy code

---

## 🔐 CORS Configuration

- Backend allows cross-origin requests for frontend integration
- Configured using `@CrossOrigin`

---

## 🧠 Key Learning Outcomes

- REST API design using Spring Boot
- DTO usage for clean data transfer
- Ingredient-based recommendation logic
- Angular standalone components
- Responsive UI design
- API integration with external services
- Safe delete operations in databases

---

## 🏁 Conclusion

This project demonstrates a **complete end-to-end web application**, combining backend API development with a responsive frontend.  
It focuses on **real-world usability**, clean architecture, and scalable design.

---

## 👤 Author

**Recipe Finder Project**  
Developed as part of learning **Java Full Stack Development**.
