# Kotlin Quickstart - Spring Boot with PostgreSQL

A Spring Boot application built with Kotlin and PostgreSQL, featuring a complete REST API with proper layering, validation, and testing.

## Features

- **Spring Boot 3.2.0** with Kotlin
- **PostgreSQL** database with Flyway migrations
- **JPA/Hibernate** for data persistence
- **RESTful API** with proper HTTP status codes
- **Input validation** using Bean Validation
- **Global exception handling** with consistent error responses
- **Comprehensive testing** with TestContainers
- **Docker Compose** for easy PostgreSQL and pgAdmin setup

## Prerequisites

- Java 17 or higher
- Gradle 8.5 or higher
- Docker and Docker Compose (for PostgreSQL)

## Quick Start

### 1. Start PostgreSQL and pgAdmin

```bash
docker-compose up -d
```

This will start:
- **PostgreSQL** on `localhost:5432` with:
  - Database: `kotlin_quickstart`
  - Username: `postgres`
  - Password: `postgres`
- **pgAdmin** on `http://localhost:8081` with:
  - Email: `admin@example.com`
  - Password: `admin`

### 2. Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 3. Access pgAdmin (Optional)

You can access the pgAdmin web interface at `http://localhost:8081` to manage your database:

1. Login with:
   - Email: `admin@example.com`
   - Password: `admin`

2. The PostgreSQL server should be pre-configured as "kotlin-quickstart"
   - If not, add a new server connection:
   - **Name**: `kotlin-quickstart`
   - **Host**: `postgres` (use the service name, not localhost)
   - **Port**: `5432`
   - **Database**: `kotlin_quickstart`
   - **Username**: `postgres`
   - **Password**: `postgres`

**Important**: Use `postgres` as the hostname (not localhost or 127.0.0.1) since both containers are on the same Docker network.

### 4. Test the API

#### Create a user:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com"
  }'
```

#### Get all users:
```bash
curl http://localhost:8080/api/users
```

#### Get user by ID:
```bash
curl http://localhost:8080/api/users/550e8400-e29b-41d4-a716-446655440000
```

#### Update user:
```bash
curl -X PUT http://localhost:8080/api/users/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "email": "john.smith@example.com"
  }'
```

#### Delete user:
```bash
curl -X DELETE http://localhost:8080/api/users/550e8400-e29b-41d4-a716-446655440000
```

## Project Structure

```
src/
├── main/
│   ├── kotlin/com/example/kotlinquickstart/
│   │   ├── KotlinQuickstartApplication.kt    # Main application class
│   │   ├── controller/
│   │   │   └── UserController.kt             # REST endpoints
│   │   ├── service/
│   │   │   └── UserService.kt                # Business logic
│   │   ├── repository/
│   │   │   └── UserRepository.kt             # Data access
│   │   ├── entity/
│   │   │   └── User.kt                       # JPA entity
│   │   ├── dto/
│   │   │   └── UserDto.kt                    # Data transfer objects
│   │   └── exception/
│   │       └── GlobalExceptionHandler.kt     # Error handling
│   └── resources/
│       ├── application.yml                   # Configuration
│       └── db/migration/                     # Flyway migrations
├── test/
│   └── kotlin/com/example/kotlinquickstart/
│       └── unit/                             # Unit tests (domain classes)
│           ├── entity/
│           └── dto/
└── integrationTest/
    └── kotlin/com/example/kotlinquickstart/
        ├── integration/                      # Integration tests
        │   ├── KotlinQuickstartApplicationIntegrationTest.kt
        │   ├── controller/
        │   │   └── UserControllerIntegrationTest.kt
        │   ├── repository/
        │   └── service/
        └── resources/
            └── application-test.yml          # Test configuration
```

## Development

### Running Tests

The project has separate unit and integration tests:

#### Unit Tests (Domain classes only - Fast, no Spring context)
```bash
./gradlew unitTest
```

#### Integration Tests (Persistence and controllers - Slower, with Spring context and database)
```bash
./gradlew integrationTest
```

#### All Tests
```bash
./gradlew test
./gradlew check
```

### Building the Application

```bash
./gradlew build
```

### Running with Custom Configuration

You can override the database configuration by setting environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/my_database
export SPRING_DATASOURCE_USERNAME=my_user
export SPRING_DATASOURCE_PASSWORD=my_password
./gradlew bootRun
```

## API Documentation

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a new user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Request/Response Examples

#### Create User Request
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

#### User Response
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

#### Error Response
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "User with email john.doe@example.com already exists",
  "path": "/api"
}
```

## Database Migrations

The application uses Flyway for database migrations. Migrations are located in `src/main/resources/db/migration/` and are automatically applied when the application starts.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## License

This project is licensed under the MIT License.