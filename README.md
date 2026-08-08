# 🔐 AuthService – Spring Boot Authentication & Authorization

A backend authentication and authorization service built using **Java, Spring Boot, Spring Security, JWT, PostgreSQL, and JPA**.

This project was developed to understand and implement real-world backend security concepts step by step, including user registration, authentication, JWT-based authorization, role-based access control, validation, exception handling, and standardized API responses.

---

## 🚀 Features

### Authentication

* ✅ User Registration
* ✅ User Login
* ✅ BCrypt Password Hashing
* ✅ Spring Security Authentication
* ✅ JWT Token Generation
* ✅ JWT Token Validation
* ✅ JWT Authentication Filter
* ✅ Stateless Authentication

### Authorization

* ✅ Role-Based Authorization
* ✅ `USER` and `ADMIN` roles
* ✅ Protected User APIs
* ✅ Protected Admin APIs
* ✅ Authority-based access control using Spring Security

### API & Backend Features

* ✅ DTO-based request handling
* ✅ DTO Validation
* ✅ Global Exception Handling
* ✅ Custom Exceptions
* ✅ Standardized API Responses
* ✅ Swagger/OpenAPI API Documentation
* ✅ PostgreSQL Database
* ✅ Spring Data JPA
* ✅ REST APIs

---

## 🛠️ Tech Stack

| Technology        | Usage                          |
| ----------------- | ------------------------------ |
| Java 17/21        | Programming Language           |
| Spring Boot       | Backend Framework              |
| Spring Security   | Authentication & Authorization |
| JWT               | Token-Based Authentication     |
| Spring Data JPA   | Database Access                |
| Hibernate         | ORM                            |
| PostgreSQL        | Relational Database            |
| Lombok            | Boilerplate Reduction          |
| Swagger / OpenAPI | API Documentation              |
| Maven             | Build & Dependency Management  |
| Git & GitHub      | Version Control                |

---

## 🏗️ Project Architecture

```text
Client / Swagger
       │
       ▼
 REST Controller
       │
       ▼
     DTO
       │
       ▼
   Validation
       │
       ▼
 Service Layer
       │
       ▼
 Repository Layer
       │
       ▼
 PostgreSQL
```

### Security Flow

```text
                Login Request
                     │
                     ▼
             AuthenticationManager
                     │
                     ▼
          CustomUserDetailsService
                     │
                     ▼
                User Database
                     │
                     ▼
             Password Verification
                     │
                     ▼
                JWT Generation
                     │
                     ▼
                Access Token
                     │
                     ▼
          Client sends Bearer Token
                     │
                     ▼
          JwtAuthenticationFilter
                     │
                     ▼
              JWT Validation
                     │
                     ▼
           SecurityContextHolder
                     │
                     ▼
           Role / Authority Check
                     │
              ┌──────┴──────┐
              ▼             ▼
            USER          ADMIN
              │             │
              ▼             ▼
          User APIs      Admin APIs
```

---

# 🔑 Authentication Flow

## 1. Registration

A new user registers using:

```http
POST /auth/register
```

Example request:

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password@123"
}
```

The password is never stored as plain text.

It is encrypted using **BCrypt PasswordEncoder** before being stored in PostgreSQL.

New users are assigned:

```text
USER
```

by default.

---

## 2. Login

User logs in using:

```http
POST /auth/login
```

Example:

```json
{
  "email": "john@example.com",
  "password": "Password@123"
}
```

Spring Security authenticates the credentials.

After successful authentication, the application generates a JWT access token.

Example response:

```json
{
  "success": true,
  "message": "Login Successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

---

# 🔐 JWT Authentication

For protected APIs, the client sends the JWT using the HTTP Authorization header:

```http
Authorization: Bearer <JWT_TOKEN>
```

The custom:

```text
JwtAuthenticationFilter
```

intercepts the request.

It:

1. Extracts the `Authorization` header.
2. Checks for the `Bearer` prefix.
3. Extracts the JWT.
4. Extracts the username/email from the token.
5. Loads the user using `CustomUserDetailsService`.
6. Validates the JWT.
7. Creates an authenticated `UsernamePasswordAuthenticationToken`.
8. Stores authentication in `SecurityContextHolder`.
9. Allows the request to continue.

---

# 👥 Role-Based Authorization

The application supports two roles:

```text
USER
ADMIN
```

Roles are stored in the database using:

```java
@Enumerated(EnumType.STRING)
private Role role;
```

New registrations receive:

```java
.role(Role.USER)
```

Users cannot choose their own role during registration because allowing this would allow anyone to register as an administrator.

---

## Authorization Rules

```text
/auth/register       → Public
/auth/login          → Public

/user/**             → USER + ADMIN

/admin/**            → ADMIN only
```

Spring Security configuration uses authorities:

```java
.requestMatchers("/admin/**")
.hasAuthority("ADMIN")

.requestMatchers("/user/**")
.hasAnyAuthority("USER", "ADMIN")
```

### Example

A `USER` accessing:

```http
GET /user/profile
```

will receive:

```text
200 OK
```

But a `USER` accessing:

```http
GET /admin/dashboard
```

will receive:

```text
403 Forbidden
```

An `ADMIN` can access both.

---

# 🛡️ Password Security

Passwords are hashed using BCrypt before being persisted.

Example:

```text
Plain Password
      │
      ▼
BCryptPasswordEncoder
      │
      ▼
Hashed Password
      │
      ▼
PostgreSQL
```

The original password is never stored in the database.

---

# ✅ DTO Validation

The application uses Jakarta Bean Validation.

Example:

```java
@NotBlank
private String name;

@NotBlank
@Email
private String email;

@NotBlank
@Size(min = 6, max = 20)
private String password;
```

The controller uses:

```java
@Valid
```

to trigger validation.

Invalid requests are handled centrally through the global exception handler.

---

# 🚨 Global Exception Handling

The application uses:

```java
@RestControllerAdvice
```

to handle exceptions globally.

Currently handled exceptions include:

* `UserAlreadyExistsException`
* `MethodArgumentNotValidException`
* `RuntimeException`

Example validation response:

```json
{
  "timestamp": "2026-08-08T10:30:00",
  "status": 400,
  "message": "Name is required"
}
```

Example duplicate-user response:

```json
{
  "timestamp": "2026-08-08T10:30:00",
  "status": 409,
  "message": "Email already registered"
}
```

---

# 📦 Standard API Response

Successful APIs use a generic response wrapper:

```java
ApiResponse<T>
```

Structure:

```json
{
  "success": true,
  "message": "Login Successful",
  "data": {}
}
```

### Registration Response

```json
{
  "success": true,
  "message": "User Registered Successfully",
  "data": null
}
```

### Login Response

```json
{
  "success": true,
  "message": "Login Successful",
  "data": {
    "token": "eyJhbGc..."
  }
}
```

The generic response allows different APIs to return different data types while maintaining the same response structure.

---

# 📚 API Endpoints

## Authentication

| Method | Endpoint         | Authentication |
| ------ | ---------------- | -------------- |
| POST   | `/auth/register` | Public         |
| POST   | `/auth/login`    | Public         |

## User

| Method | Endpoint        | Required Role |
| ------ | --------------- | ------------- |
| GET    | `/user/profile` | USER / ADMIN  |

## Admin

| Method | Endpoint           | Required Role |
| ------ | ------------------ | ------------- |
| GET    | `/admin/dashboard` | ADMIN         |

---

# 📖 Swagger API Documentation

Swagger/OpenAPI is integrated for API testing and documentation.

After starting the application, open:

```text
http://localhost:8080/swagger-ui/index.html
```

You can:

1. Register a user.
2. Login.
3. Copy the JWT token.
4. Click **Authorize**.
5. Enter:

```text
Bearer <your-token>
```

6. Test protected endpoints.

---

# 🗄️ Database

The project uses PostgreSQL.

### User Table

The `users` table contains information such as:

```text
id
name
email
password
role
```

Example:

| id | name  | email                                         | role  |
| -- | ----- | --------------------------------------------- | ----- |
| 1  | John  | [john@example.com](mailto:john@example.com)   | USER  |
| 2  | Admin | [admin@example.com](mailto:admin@example.com) | ADMIN |

---

# ⚙️ Configuration

Update your `application.properties` with your PostgreSQL configuration.

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/authservice
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.jpa.properties.hibernate.format_sql=true
```

> Do not commit real database passwords, JWT secrets, API keys, or other credentials to GitHub.

For production, these values should be provided through environment variables or a secrets manager.

---

# ▶️ How to Run

## 1. Clone the repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

## 2. Open the project

Open the project using IntelliJ IDEA or another Java IDE.

## 3. Configure PostgreSQL

Create the database:

```sql
CREATE DATABASE authservice;
```

Update your database credentials in:

```text
application.properties
```

## 4. Build the project

```bash
mvn clean install
```

## 5. Run the application

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application class from IntelliJ.

## 6. Open Swagger

```text
http://localhost:8080/swagger-ui/index.html
```

---

# 🧪 Testing Flow

A complete authentication test can be performed in this order:

```text
1. Register User
       ↓
2. Login User
       ↓
3. Receive JWT
       ↓
4. Click Swagger Authorize
       ↓
5. Enter Bearer Token
       ↓
6. Access /user/profile
       ↓
7. Access /admin/dashboard
       ↓
8. USER → 403 Forbidden
       ↓
9. ADMIN → 200 OK
```

---

# 📂 Project Structure

```text
src
└── main
    └── java
        └── com.example.authservice
            │
            ├── controller
            │   ├── AuthController.java
            │   ├── UserController.java
            │   └── AdminController.java
            │
            ├── dto
            │   ├── RegisterRequest.java
            │   ├── LoginRequest.java
            │   ├── LoginResponse.java
            │   └── ApiResponse.java
            │
            ├── entity
            │   ├── User.java
            │   ├── Role.java
            │   └── RefreshToken.java
            │
            ├── Repository
            │   └── UserRepository.java
            │
            ├── service
            │   ├── AuthService.java
            │   ├── AuthServiceImpl.java
            │   ├── UserService.java
            │   └── UserServiceImpl.java
            │
            ├── security
            │   ├── JwtService.java
            │   ├── JwtAuthenticationFilter.java
            │   ├── CustomUserDetails.java
            │   └── CustomUserDetailsService.java
            │
            ├── exception
            │   ├── ApiError.java
            │   ├── GlobalExceptionHandler.java
            │   └── UserAlreadyExistsException.java
            │
            └── config
                └── SecurityConfig.java
```

> Update the structure if your actual package/class names differ.

---

# 🔒 Security Considerations

This project follows several security practices:

* Passwords are hashed using BCrypt.
* JWT authentication is stateless.
* Protected APIs require authentication.
* Role-based authorization prevents unauthorized access.
* Users cannot select `ADMIN` during public registration.
* Validation prevents invalid request data.
* Global exception handling provides controlled error responses.
* Sensitive credentials should not be committed to Git.

For a production deployment, JWT secrets and database credentials should be moved to environment variables or a dedicated secrets-management solution.

---

# 🚧 Future Enhancements

The following features are planned for the next development phases:

* ⏳ Refresh Token
* ⏳ Refresh Token Rotation / Revocation
* ⏳ Logout with Refresh Token Invalidation
* ⏳ OAuth2 / Google Login
* ⏳ Angular Frontend Integration
* ⏳ Docker
* ⏳ Docker Compose
* ⏳ CI/CD
* ⏳ Production deployment

These are intentionally listed as **planned**, not implemented yet.

---

# 🎯 Learning Objectives

This project was created to gain practical experience with:

* Spring Boot REST API development
* Spring Security
* JWT Authentication
* JWT Authorization
* Role-Based Access Control
* BCrypt Password Hashing
* DTO Validation
* Global Exception Handling
* Generic API Response Design
* PostgreSQL
* JPA / Hibernate
* Swagger/OpenAPI
* Git/GitHub

---

# 👨‍💻 Author

**Dheeraj Potdar**

Software Developer | Java | Spring Boot | Spring Security | Microservices | REST APIs

---

## ⭐ If you find this project useful

Feel free to explore the repository and follow the development journey as additional authentication and deployment features are implemented.
