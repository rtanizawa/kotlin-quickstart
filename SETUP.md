# Setup Instructions

## Start PostgreSQL and pgAdmin

```bash
docker-compose up -d
```

This will start:
- **PostgreSQL** on `localhost:5432`
- **pgAdmin** on `http://localhost:8081` (admin@example.com / admin)

**Note**: When connecting to PostgreSQL from pgAdmin, use `postgres` as the hostname (not localhost).

## Run the Application

Once the Gradle wrapper is set up:

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## Test the API

Create a user:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com"
  }'
```

Get all users:
```bash
curl http://localhost:8080/api/users
```

## Project Features

✅ Spring Boot 3.2.0 with Kotlin  
✅ PostgreSQL database with Flyway migrations  
✅ JPA/Hibernate for data persistence  
✅ RESTful API with proper HTTP status codes  
✅ Input validation using Bean Validation  
✅ Global exception handling  
✅ Comprehensive testing with TestContainers  
✅ Docker Compose for PostgreSQL  
✅ Complete project structure with proper layering  

The project is ready to use once you complete the Gradle wrapper setup! 