# Choroid Authentication & Authorization Service

A secure, modern Spring Boot microservice providing JWT-based authentication and authorization capabilities for the Choroid DDBS project.

## 🚀 Features

- **JWT Authentication**: Secure token-based authentication with refresh tokens
- **User Registration**: Secure user signup with password hashing (BCrypt)
- **Input Validation**: Comprehensive request validation with detailed error messages
- **Modern Security**: Latest Spring Security 6+ with proper configuration
- **Error Handling**: Global exception handling with structured error responses
- **Database Integration**: JPA/Hibernate with MySQL support
- **Logging**: Structured logging with SLF4J
- **API Documentation**: RESTful API design with proper DTOs
- **Environment Configuration**: Externalized configuration for different environments

## 📋 Prerequisites

- **Java 24**: OpenJDK or Oracle JDK 24+
- **MySQL 8.0+**: Database server
- **Maven/Gradle**: Build tool (Gradle included)
- **Git**: Version control

## 🛠️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd choroid-auth-service
```

### 2. Database Setup

Create a MySQL database and user:

```sql
-- Create database
CREATE DATABASE choroid_auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'choroid_user'@'localhost' IDENTIFIED BY 'S3cureP@ssw0rd!';
GRANT ALL PRIVILEGES ON choroid_auth_db.* TO 'choroid_user'@'localhost';
FLUSH PRIVILEGES;

-- Run the schema (optional - JPA will auto-create)
USE choroid_auth_db;
SOURCE src/main/resources/schema.sql;
```

### 3. Environment Configuration

Set up environment variables (recommended for production):

```bash
# Database Configuration
export DB_URL=jdbc:mysql://localhost:3306/choroid_auth_db
export DB_USERNAME=choroid_user
export DB_PASSWORD=S3cureP@ssw0rd!

# JWT Configuration (IMPORTANT: Change in production!)
export JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-make-it-at-least-32-chars
export JWT_EXPIRATION=86400000
export JWT_REFRESH_EXPIRATION=604800000

# Server Configuration
export SERVER_PORT=8080
```

### 4. Build & Run

```bash
# Build the application
./gradlew build

# Run the application
./gradlew bootRun

# Or run the JAR
java -jar build/libs/choroid-auth-service-0.0.1-SNAPSHOT.jar
```

The service will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints

#### 1. Health Check
```http
GET /api/auth/health
```

**Response:**
```json
{
  "status": "UP",
  "service": "Choroid Authentication Service",
  "timestamp": "2025-01-12T12:00:00"
}
```

#### 2. User Registration
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response (201 Created):**
```json
{
  "message": "User registered successfully",
  "username": "john_doe",
  "timestamp": "2025-01-12T12:00:00"
}
```

#### 3. User Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "issuedAt": "2025-01-12T12:00:00",
  "expiresAt": "2025-01-13T12:00:00",
  "message": "Login successful"
}
```

#### 4. Token Validation
```http
POST /api/auth/validate?token=eyJhbGciOiJIUzI1NiJ9...
```

**Response (200 OK):**
```json
{
  "valid": true,
  "username": "john_doe",
  "message": "Token is valid"
}
```

### Error Responses

#### Validation Error (400 Bad Request)
```json
{
  "timestamp": "2025-01-12T12:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed",
  "path": "/api/auth/signup",
  "validationErrors": {
    "username": "Username must be between 3 and 50 characters",
    "password": "Password must be at least 6 characters long"
  }
}
```

#### Authentication Error (401 Unauthorized)
```json
{
  "timestamp": "2025-01-12T12:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login"
}
```

#### User Exists Error (409 Conflict)
```json
{
  "timestamp": "2025-01-12T12:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Username 'john_doe' is already taken",
  "path": "/api/auth/signup"
}
```

## 🔧 Configuration

### Application Properties

The service uses environment variables for configuration. Default values are provided in `application.properties`:

```properties
# Application Configuration
spring.application.name=Choroid-Authentication-and-Authorization-Service
server.port=${SERVER_PORT:8080}

# Database Configuration
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/choroid_auth_db}
spring.datasource.username=${DB_USERNAME:choroid_user}
spring.datasource.password=${DB_PASSWORD:S3cureP@ssw0rd!}

# JWT Configuration
jwt.secret=${JWT_SECRET:your-256-bit-secret-key-here-change-this-in-production}
jwt.expiration=${JWT_EXPIRATION:86400000}
jwt.refresh-expiration=${JWT_REFRESH_EXPIRATION:604800000}
```

### Security Settings

- **Password Encoding**: BCrypt with strength 12
- **JWT Algorithm**: HS256 with 256-bit secret key
- **Session Management**: Stateless (JWT-only)
- **CORS**: Enabled for all origins (configure for production)
- **CSRF**: Disabled (appropriate for JWT-based API)

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# Run specific test
./gradlew test --tests "AuthServiceTest"
```

### Manual Testing with curl

```bash
# Health check
curl -X GET http://localhost:8080/api/auth/health

# Register user
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'

# Validate token
curl -X POST "http://localhost:8080/api/auth/validate?token=YOUR_TOKEN_HERE"
```

## 🔒 Security Considerations

### Production Deployment Checklist

- [ ] **Change JWT Secret**: Use a strong, randomly generated 256-bit secret
- [ ] **Environment Variables**: Never hardcode secrets in production
- [ ] **Database Credentials**: Use secure, unique database credentials
- [ ] **CORS Configuration**: Restrict allowed origins for your domains
- [ ] **HTTPS**: Always use HTTPS in production
- [ ] **Rate Limiting**: Implement rate limiting for auth endpoints
- [ ] **Monitoring**: Set up logging and monitoring
- [ ] **Database Security**: Use connection pooling and SSL connections

### Default Test Users

The schema includes test users (remove in production):

- **admin**: password `admin123`
- **testuser1**: password `password123`
- **testuser2**: password `password123`

## 📊 Database Schema

### Credentials Table
```sql
CREATE TABLE credentials (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Login Attempts Table (Optional Audit)
```sql
CREATE TABLE login_attempts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    ip_address VARCHAR(45),
    success BOOLEAN NOT NULL,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    error_message TEXT
);
```

## 🚀 Deployment

### Docker Deployment

Create a `Dockerfile`:

```dockerfile
FROM openjdk:24-jre-slim
COPY build/libs/choroid-auth-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:
```bash
docker build -t choroid-auth-service .
docker run -p 8080:8080 -e JWT_SECRET=your-secret choroid-auth-service
```

### Production Environment Variables

```bash
# Database
DB_URL=jdbc:mysql://prod-db:3306/choroid_auth_db
DB_USERNAME=prod_user
DB_PASSWORD=super_secure_password

# JWT (256-bit secret)
JWT_SECRET=your-super-secure-256-bit-jwt-secret-key-change-this
JWT_EXPIRATION=3600000  # 1 hour
JWT_REFRESH_EXPIRATION=86400000  # 24 hours

# Server
SERVER_PORT=8080
LOG_LEVEL=INFO
JPA_DDL_AUTO=validate  # Don't auto-create in production
JPA_SHOW_SQL=false
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is part of the Choroid DDBS system. All rights reserved.

## 📞 Support

For support and questions, please contact the development team or create an issue in the repository.

---

## 🔄 Changelog

### Version 0.0.1-SNAPSHOT
- Initial release with JWT authentication
- User registration and login
- JPA integration with MySQL
- Comprehensive error handling
- Modern Spring Security configuration
- Input validation and DTOs
- Structured logging
