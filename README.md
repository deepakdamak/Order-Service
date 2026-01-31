# Order Service

Spring Boot REST API for placing orders with stock validation and JWT security.

## Features
- Place Order API
- Prevent overselling using database locking
- JWT authentication
- Global exception handling
- Pagination support
- Audit fields (createdAt, updatedAt)

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Security + JWT
- PostgreSQL
- Spring Data JPA

## Setup
1. Clone the project
2. Configure PostgreSQL in `application.properties`
3. Run the application

## APIs

### Auth
POST /auth/login

Request:
```json
{
  "email": "dipak@wdipl.com",
  "password": "123456"
}
