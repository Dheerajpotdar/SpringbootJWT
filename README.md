# 🔐 Spring Boot JWT Authentication & Authorization System

A secure REST API backend built with **Java, Spring Boot, Spring Security, JWT, PostgreSQL, and Docker**. This project demonstrates a complete authentication and authorization flow including user registration, login, JWT-based authentication, role-based authorization, refresh tokens, logout, validation, exception handling, API documentation, and containerization.

---

## 🚀 Features

* User Registration
* User Login
* BCrypt Password Hashing
* Spring Security Authentication
* JWT Access Token Generation
* JWT Token Validation
* JWT Authentication Filter
* Protected REST APIs
* Role-Based Authorization (`USER` / `ADMIN`)
* Refresh Token
* Access Token Renewal
* Refresh Token Expiration Validation
* Logout with Refresh Token Invalidation
* Request Validation
* Global Exception Handling
* Standardized API Responses
* PostgreSQL Database
* Spring Data JPA & Hibernate
* Swagger/OpenAPI Documentation
* Docker
* Docker Compose

---

## 🏗️ Architecture / Flow

```text
                    Client
                      │
                      ▼
              Spring Boot REST API
                      │
          ┌───────────┴───────────┐
          │                       │
          ▼                       ▼
     Registration               Login
          │                       │
          ▼                       ▼
   BCrypt Password          AuthenticationManager
      Hashing                      │
          │                       ▼
          ▼                 Spring Security
      PostgreSQL                   │
                                  ▼
                            JWT Generation
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
                    ▼                           ▼
              Access Token               Refresh Token
                    │                           │
                    ▼                           ▼
             Protected APIs              /auth/refresh
                    │                           │
                    ▼                           ▼
             JWT Filter                  New Access Token
                    │
                    ▼
             Role Authorization
              USER / ADMIN
                    │
                    ▼
                  Logout
                    │
                    ▼
        Refresh Token Invalidation
```

---

## 🛠️ Technologies Used

| Technology         | Purpose                        |
| ------------------ | ------------------------------ |
| Java 17            | Programming Language           |
| Spring Boot        | Backend Framework              |
| Spring MVC         | REST API Development           |
| Spring Security    | Authentication & Authorization |
| JWT                | Stateless Authentication       |
| BCrypt             | Password Hashing               |
| Spring Data JPA    | Database Access                |
| Hibernate          | ORM                            |
| PostgreSQL         | Relational Database            |
| Jakarta Validation | Request Validation             |
| Swagger / OpenAPI  | API Documentation              |
| Maven              | Build & Dependency Management  |
| Docker             | Application Containerization   |
| Docker Compose     | Multi-container Setup          |
| Git & GitHub       | Version Control                |

---

# 🔑 Authentication Flow

## 1. User Registration

The user sends registration details:

```http
POST /auth/register
```

Example:

```json
{
  "name": "John",
  "email": "john@example.com",
  "password": "Password@123"
}
```

The password is never stored as plain text.

It is hashed using **BCrypt** before being stored in PostgreSQL.

```text
Password
   ↓
BCryptPasswordEncoder
   ↓
Hashed Password
   ↓
PostgreSQL
```

New users are assigned the default `USER` role.

---

## 2. User Login

```http
POST /auth/login
```

The user provides their email and password.

```text
Login Request
     ↓
AuthenticationManager
     ↓
UserDetailsService
     ↓
User lookup
     ↓
BCrypt password verification
     ↓
Authentication successful
     ↓
JWT Access Token
     +
Refresh Token
```

---

# 🔐 JWT Authentication

After successful login, the server generates an access token.

The client sends the access token in subsequent requests:

```http
Authorization: Bearer <access-token>
```

The JWT authentication filter:

1. Reads the `Authorization` header
2. Extracts the JWT
3. Validates the token
4. Extracts the username/email
5. Loads the user details
6. Creates an authenticated `SecurityContext`
7. Allows the request to continue

```text
Client
  │
  │ Authorization: Bearer JWT
  ▼
JWT Authentication Filter
  │
  ├── Extract Token
  ├── Validate Token
  ├── Extract User
  └── Authenticate User
  │
  ▼
Spring Security
  │
  ▼
Protected Controller
```

---

# 👥 Role-Based Authorization

The application supports:

```text
USER
ADMIN
```

A normal user receives the `USER` role during registration.

Administrative access is controlled through Spring Security authorization rules.

Example:

```text
USER
 └── Access USER APIs

ADMIN
 ├── Access USER APIs
 └── Access ADMIN APIs
```

The role is stored with the user and included in the authenticated user's authorities.

Spring Security then checks these authorities before allowing access to protected endpoints.

---

# 🔄 Refresh Token Flow

Access tokens are short-lived for security reasons.

Instead of asking the user to log in again after an access token expires, the application uses a refresh token.

```text
Access Token Expired
        ↓
Client sends Refresh Token
        ↓
Refresh Token Validation
        ↓
Check Expiration
        ↓
Find Associated User
        ↓
Generate New Access Token
        ↓
Client continues using API
```

Example:

```http
POST /auth/refresh
```

The refresh token is validated before generating a new access token.

---

# 🚪 Logout Flow

The application also supports logout.

```http
POST /auth/logout
```

During logout:

```text
Refresh Token
      ↓
Find stored token
      ↓
Invalidate / Delete token
      ↓
User logged out
```

This prevents the invalidated refresh token from being reused to generate new access tokens.

---

# 🗄️ Database

The application uses:

```text
PostgreSQL
   │
   ├── users
   │
   └── refresh_tokens
```

Spring Data JPA and Hibernate are used for database interaction.

Hibernate automatically manages the entity-to-table mapping.

---

# ✅ Request Validation

The application uses Jakarta Bean Validation for validating incoming requests.

Examples include:

```java
@NotBlank
@Email
@Size
```

Invalid requests are handled consistently through the global exception handler.

---

# ⚠️ Global Exception Handling

The application uses:

```java
@RestControllerAdvice
```

to provide centralized exception handling.

Examples include:

* User already exists
* Invalid requests
* Runtime exceptions
* Authentication-related errors
* Validation errors

Example response:

```json
{
  "timestamp": "2026-08-08T12:30:00",
  "status": 409,
  "message": "User already exists"
}
```

---

# 📦 Standard API Response

Successful APIs return a consistent response structure.

Example:

```json
{
  "success": true,
  "message": "Login Successful",
  "data": {
    "accessToken": "JWT_ACCESS_TOKEN",
    "refreshToken": "REFRESH_TOKEN"
  }
}
```

This provides a predictable structure for API consumers.

---

# 📚 API Documentation

Swagger/OpenAPI is integrated for API documentation and testing.

After starting the application, Swagger UI can be accessed at:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger can be used to test:

* Registration
* Login
* Refresh Token
* Logout
* Protected APIs
* Role-based APIs

---

# 🐳 Docker

The application is containerized using Docker.

The project contains:

```text
Dockerfile
docker-compose.yml
```

The Docker setup contains two services:

```text
Docker Compose
      │
      ├── Spring Boot
      │      └── authservice
      │
      └── PostgreSQL
             └── authServicedb
```

---

## 🐳 Dockerfile

The Spring Boot application is packaged as a JAR and executed inside a Java 17 Docker container.

```text
Spring Boot Application
        ↓
    Maven Build
        ↓
       JAR
        ↓
    Docker Image
        ↓
 Docker Container
```

---

# ▶️ Running the Project Locally

## Prerequisites

Make sure you have installed:

* Java 17
* Maven
* PostgreSQL
* Docker Desktop
* Git

---

## 1. Clone the Repository

```bash
git clone https://github.com/Dheerajpotdar/SpringbootJWT.git
```

Navigate to the project:

```bash
cd SpringbootJWT
```

---

## 2. Configure Database

For local execution, configure PostgreSQL in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/authServicedb
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Never commit real database passwords or secrets to GitHub.**

---

# ▶️ Run Using Maven

Build the project:

```bash
mvn clean package
```

Run the application:

```bash
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

---

# 🐳 Run Using Docker Compose

Make sure Docker Desktop is running.

Run:

```bash
docker compose up --build
```

This will:

1. Build the Spring Boot Docker image
2. Start PostgreSQL
3. Start the Spring Boot application
4. Create the Docker network
5. Connect Spring Boot with PostgreSQL

To stop the containers:

```bash
docker compose down
```

To see running containers:

```bash
docker ps
```

---

# 🧪 Testing the Authentication Flow

Recommended testing sequence:

### Step 1 — Register

```http
POST /auth/register
```

Create a new user.

### Step 2 — Login

```http
POST /auth/login
```

Receive:

```text
Access Token
Refresh Token
```

### Step 3 — Access Protected API

Add:

```http
Authorization: Bearer <access-token>
```

### Step 4 — Refresh Access Token

```http
POST /auth/refresh
```

Send the refresh token.

A new access token is generated.

### Step 5 — Logout

```http
POST /auth/logout
```

The refresh token is invalidated.

---

# 📁 Project Structure

```text
src
└── main
    ├── java
    │   └── com.example.authservice
    │       │
    │       ├── Controller
    │       ├── Repository
    │       ├── Service
    │       ├── dto
    │       ├── entity
    │       ├── exception
    │       ├── security
    │       └── AuthserviceApplication.java
    │
    └── resources
        └── application.properties

Dockerfile
docker-compose.yml
pom.xml
README.md
```

---

# 🔒 Security Considerations

The project follows several security practices:

* Passwords are hashed using BCrypt
* Passwords are never stored as plain text
* JWT is used for stateless authentication
* Protected APIs require authentication
* Role-based authorization restricts administrative APIs
* Refresh tokens are validated before generating new access tokens
* Refresh tokens are invalidated during logout
* Secrets should be provided through environment variables rather than committed to source control

---

# 🎯 Learning Objectives

This project was built to gain practical understanding of:

* Spring Security architecture
* Authentication vs Authorization
* JWT authentication
* Stateless security
* JWT filters
* UserDetails and UserDetailsService
* AuthenticationManager
* BCrypt password hashing
* Role-based access control
* Refresh token architecture
* Token expiration
* Logout and token invalidation
* Exception handling
* REST API design
* PostgreSQL with JPA/Hibernate
* API documentation
* Docker containerization
* Docker Compose
* Container-to-container networking

---

# 🔗 GitHub

**Repository:**

https://github.com/Dheerajpotdar/SpringbootJWT

---

# 👨‍💻 Author

**Dheeraj Potdar**

Software Developer | Java | Spring Boot | Spring Security | REST APIs | Microservices

---

## ⭐ Project Highlights

```text
Spring Boot
     ↓
REST APIs
     ↓
PostgreSQL + JPA
     ↓
Registration
     ↓
BCrypt Password Hashing
     ↓
Login
     ↓
Spring Security
     ↓
JWT Generation
     ↓
JWT Validation
     ↓
JWT Authentication Filter
     ↓
Protected APIs
     ↓
DTO Validation
     ↓
Global Exception Handling
     ↓
Standard API Responses
     ↓
Role-Based Authorization
     ↓
Refresh Tokens
     ↓
Logout / Token Invalidation
     ↓
Docker
     ↓
Docker Compose
     ↓
Spring Boot + PostgreSQL Containers
```
