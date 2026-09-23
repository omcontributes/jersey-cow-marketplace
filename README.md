# 🐄 Jersey Cow Marketplace

A production-style Spring Boot REST API for a farmer-to-buyer marketplace where farmers list Jersey cows for sale with images, and buyers browse, search, and contact farmers directly.

Built as a portfolio project to demonstrate real-world Spring Boot backend development practices: JWT authentication, role-based authorization, ownership validation, dynamic search/filtering, file upload handling, and clean layered architecture.

---

## Features

**Farmers can:**
- Register and log in
- Create cow listings with a minimum of 3 images
- Update, delete, and mark their own listings as sold
- View buyers who contacted them about their cows

**Buyers can:**
- Register and log in
- Browse and search available cows with filters (breed, price range, city, age, milk production)
- View full cow details including farmer contact info
- Contact a farmer directly via Call/WhatsApp links, or send a message

**Cross-cutting:**
- JWT-based stateless authentication
- Role-based authorization (FARMER / BUYER)
- Strict ownership validation — a farmer can never modify another farmer's listing
- Marathi and English language support for messages and validation errors
- Optimistic locking to prevent concurrent double-sell race conditions
- Centralized error handling with consistent JSON error responses
- Swagger/OpenAPI documentation
- Dockerized with MySQL via Docker Compose

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt) |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL 8 (H2 for tests) |
| Validation | Jakarta Bean Validation |
| Docs | Springdoc OpenAPI (Swagger UI) |
| Testing | JUnit 5, Mockito, MockMvc |
| Build | Maven |
| Containerization | Docker, Docker Compose |
| Utilities | Lombok |

---

## Architecture

Clean layered architecture, separating concerns across packages: