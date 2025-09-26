# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

The Choroid Authentication Service is a Spring Boot microservice providing JWT-based authentication and authorization. It uses direct JDBC for database operations (not JPA) and is designed for team development with Hamachi VPN support.

## Architecture

### Core Components
- **Controller Layer** (`AuthController`): REST endpoints for auth operations
- **Service Layer** (`AuthService`): Business logic for authentication
- **Repository Layer** (`CredentialsRepository`): Direct JDBC database operations
- **Security Layer** (`SecurityConfig`, `JwtUtil`): JWT token management and Spring Security configuration
- **Exception Handling**: Global exception handler with structured error responses

### Database Architecture
- **Simple schema**: Single `credentials` table with username/password
- **Direct JDBC**: Uses `JdbcTemplate` instead of JPA for performance
- **MySQL**: Running in Docker container on port 3307

### JWT Implementation
- **HS256 algorithm** with 256-bit secret key
- **Access tokens**: 24-hour expiration (configurable)
- **Refresh tokens**: 7-day expiration (configurable)
- **Stateless authentication**: No server-side sessions

## Development Commands

### Build and Run
```bash
# Build the project
.\gradlew build

# Run the application
.\gradlew bootRun

# Run with specific profile
.\gradlew bootRun -Pspring.profiles.active=dev

# Clean and rebuild
.\gradlew clean build
```

### Testing
```bash
# Run all tests
.\gradlew test

# Run tests with coverage
.\gradlew test jacocoTestReport

# Run specific test class
.\gradlew test --tests "AuthServiceTest"

# Run tests in continuous mode
.\gradlew test --continuous
```

### Database Operations
```bash
# Start MySQL container
docker-compose -f docker-compose-mysql.yml up -d

# Stop MySQL container
docker-compose -f docker-compose-mysql.yml down

# View container logs
docker logs my-mysql-db

# Connect to MySQL
mysql -h localhost -P 3307 -u root -p

# Restart MySQL for team collaboration
.\restart-mysql-vpn.ps1
```

### API Testing
```bash
# Health check
curl http://localhost:8080/api/auth/health

# User signup
curl -X POST http://localhost:8080/api/auth/signup -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"testpass123\"}"

# User login
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"testpass123\"}"

# Validate token
curl -X POST "http://localhost:8080/api/auth/validate?token=YOUR_JWT_TOKEN"
```

## Configuration Management

### Environment Variables
The application uses environment variables for configuration:
- `DB_URL`: Database connection string
- `DB_USERNAME`: Database username  
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: 256-bit secret key for JWT signing
- `JWT_EXPIRATION`: Access token expiration (milliseconds)
- `JWT_REFRESH_EXPIRATION`: Refresh token expiration (milliseconds)
- `SERVER_PORT`: Application server port

### Application Properties
Default configuration in `src/main/resources/application.properties`:
- Server runs on port 8080 (configurable via `SERVER_PORT`)
- MySQL on localhost:3307 (Docker container)
- JWT expiration: 24 hours (86400000ms)
- Refresh token expiration: 7 days (604800000ms)

## Team Development Setup

### Hamachi VPN Configuration
This project is designed for team collaboration using Hamachi VPN:

1. **Host Setup**: Creates Hamachi network `choroid-ddbs-team`
2. **Team Access**: Members join VPN to access shared MySQL database
3. **Port Coordination**: Host uses 8081, team members use 8082+
4. **Database Sharing**: Single MySQL instance accessible via VPN

### Key Files for Team Setup
- `VPN_SETUP_GUIDE.md`: Complete VPN setup instructions
- `TEAM_SETUP_CHECKLIST.md`: Connection details and checklist
- `docker-compose-mysql.yml`: MySQL container configuration

## Frontend Testing Interface

### Complete Web Interface
Located in `frontend/` directory with full JWT authentication flow:
- `app.html`: Main entry point with automatic authentication routing
- `login.html`: User authentication with demo user support
- `signup.html`: User registration with validation
- `dashboard.html`: Protected landing page for authenticated users
- `index.html`: API testing interface (manual testing)

### Starting Frontend
```bash
# Navigate to frontend
cd frontend

# Option 1: Open in browser directly
start app.html

# Option 2: Use the launcher script
start-test-environment.bat

# Option 3: Serve with HTTP server
python -m http.server 8000
```

## Code Patterns and Standards

### Exception Handling
- Custom exceptions: `AuthException`, `InvalidCredentialsException`, `UserAlreadyExistsException`
- Global exception handler: `GlobalExceptionHandler` provides structured error responses
- Validation errors: Automatic validation with detailed field-level errors

### Security Patterns
- **Password encoding**: BCrypt with strength 12
- **CORS configuration**: Allows all origins (configure for production)
- **Stateless sessions**: JWT-only, no server sessions
- **Input validation**: JSR-303 validation annotations on DTOs

### Repository Pattern
- Direct JDBC implementation using `JdbcTemplate`
- Custom `RowMapper` for entity mapping
- Upsert operations with `ON DUPLICATE KEY UPDATE`
- Proper exception handling for database operations

## Common Tasks

### Adding New Endpoints
1. Add method to `AuthController` with proper validation
2. Implement business logic in `AuthService`
3. Update security configuration if needed
4. Add corresponding tests

### Database Schema Changes
1. Update `src/main/resources/schema.sql`
2. Modify `Credentials` model if needed
3. Update `CredentialsRepository` queries
4. Restart Docker container to apply schema changes

### JWT Configuration Changes
1. Update properties in `application.properties`
2. Modify `JwtUtil` if algorithm or structure changes
3. Test token generation and validation
4. Update frontend if token format changes

## Development Tips

### Local Development
- Use different server ports for each team member (8082, 8083, etc.)
- Database password is in application.properties (change in production)
- JWT secret should be changed in production deployments
- Use the health endpoint to verify service is running

### Debugging
- Check application logs for authentication failures
- Use frontend testing interface for quick API validation
- Verify MySQL container is running with `docker ps`
- Test VPN connectivity with ping to host IP

### Security Considerations
- JWT secret must be at least 256 bits for HS256
- Passwords are hashed with BCrypt strength 12
- All auth endpoints use HTTPS in production
- Database credentials should be secured in production

## Production Deployment

### Environment Setup
- Set strong JWT secret (256-bit random string)
- Use secure database credentials
- Configure CORS for specific origins only
- Enable HTTPS and disable debug logging
- Use connection pooling for database

### Docker Deployment
The service can be containerized using the provided `docker-compose-mysql.yml` as a reference for MySQL configuration.