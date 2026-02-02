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
### Public Endpoints:
- POST /api/auth/login - User authentication
- POST /api/users - User creation

### Protected Endpoints (Require JWT):
- GET/POST/PUT/DELETE /api/products/** - Product management
- GET/POST /api/orders/** - Order management
- POST /api/users/** - User Creation

### Testing:
1. Create user: POST /api/users
2. Login: POST /api/auth/login
3. Use token for all other requests

### Auth
POST localhost:8080/api/auth/login

Request:
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass@123"
}

POST localhost:8080/api/orders

Request:
```json
{
  "productId": 1,
  "quantity": 2
}

POST localhost:8080/api/products

Request:
````json
{
    "name": "MacBook Pro M1",
    "stock": 20,
    "price": 199999.00
  }
POST localhost:8080/api/users

````json
{
  "email": "john.don@example.com",
  "password": "SecurePass@123",
  "role": "ADMIN",
  "subRole": "PREMIUM",
  "active": true
}

GET localhost:8080/api/orders/getAllOrders
GET localhost:8080/api/products
