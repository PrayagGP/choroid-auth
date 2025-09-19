# Choroid Authentication & Authorization Service

A secure, modern Spring Boot microservice providing JWT-based authentication and authorization capabilities for the Choroid DDBS project.

## 🚀 Features

- **JWT Authentication**: Secure token-based authentication with refresh tokens
- **User Registration**: Secure user signup with password hashing (BCrypt)
- **Input Validation**: Comprehensive request validation with detailed error messages
- **Modern Security**: Latest Spring Security 6+ with proper configuration
- **Error Handling**: Global exception handling with structured error responses
- **Database Integration**: JDBC with MySQL support (lightweight and performant)
- **Logging**: Structured logging with SLF4J
- **API Documentation**: RESTful API design with proper DTOs
- **Environment Configuration**: Externalized configuration for different environments

## 📋 Prerequisites

- **Java 24**: OpenJDK or Oracle JDK 24+
- **Docker**: For MySQL database (recommended)
- **MySQL 8.0+**: Database server (Docker or local install)
- **Gradle**: Build tool (wrapper included)
- **Git**: Version control
- **Hamachi VPN** (for team collaboration): Download from https://www.vpn.net/

## 🛠️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd choroid-auth-service
```

### 2. Database Setup (Docker - Recommended)

**Option A: Docker MySQL (Recommended)**
```bash
# Start MySQL container
docker run -d \
  --name my-mysql-db \
  -e MYSQL_ROOT_PASSWORD=[YOUR_DB_PASSWORD]
  -e MYSQL_DATABASE=choroid_db \
  -p 3307:3306 \
  -v mysql_data:/var/lib/mysql \
  -v $(pwd)/src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql \
  mysql:8.0
```

**Option B: Docker Compose**
```bash
# Use the provided docker-compose file
docker-compose -f docker-compose-mysql.yml up -d
```

**Option C: Local MySQL Installation**
```sql
-- Create database
CREATE DATABASE choroid_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Import schema
USE choroid_db;
SOURCE src/main/resources/schema.sql;
```

### 3. Environment Configuration

Set up environment variables (recommended for production):

```bash
# Database Configuration (Docker MySQL)
export DB_URL=jdbc:mysql://localhost:3307/choroid_db
export DB_USERNAME=root
export DB_PASSWORD=[YOUR_DB_PASSWORD]

# JWT Configuration (IMPORTANT: Change in production!)
export JWT_SECRET=[YOUR_JWT_SECRET_256_BIT]
export JWT_EXPIRATION=86400000
export JWT_REFRESH_EXPIRATION=604800000

# Server Configuration
export SERVER_PORT=8081
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

The service will start on `http://localhost:8081`

## 🤝 Team Collaboration Setup

This project supports secure team development using **Hamachi VPN** for database sharing.

### For Team Members (Remote Access)

1. **Install Hamachi VPN**: Download from https://www.vpn.net/
2. **Join the team network**: `choroid-ddbs-team`
3. **Get connection details** from the host
4. **Update your configuration** to connect to the host's database

### Quick Team Setup

**Host (Database Server)**:
- Creates Hamachi network: `choroid-ddbs-team`
- Runs MySQL container on port 3307
- Shares Hamachi IP with team

**Team Members**:
- Join Hamachi network
- Update database URL: `jdbc:mysql://[HOST_HAMACHI_IP]:3307/choroid_db`
- Use different server ports: 8082, 8083, etc.

### Documentation Files

- 📝 **`VPN_SETUP_GUIDE.md`** - Complete VPN setup instructions
- 📋 **`TEAM_SETUP_CHECKLIST.md`** - Connection details and checklist
- 🔍 **`docker-compose-mysql.yml`** - Alternative Docker setup

### Security Benefits

- ✅ **No port forwarding** required
- ✅ **Encrypted VPN tunnel**
- ✅ **Database not exposed** to public internet
- ✅ **Access control** via VPN network membership

## 📋 API Documentation

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
server.port=${SERVER_PORT:8081}

# Database Configuration (JDBC)
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3307/choroid_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:[YOUR_DB_PASSWORD]}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

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
curl -X GET http://localhost:8081/api/auth/health

# Register user
curl -X POST http://localhost:8081/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "[YOUR_PASSWORD]"}'

# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "[YOUR_PASSWORD]"}'

# Validate token
curl -X POST "http://localhost:8081/api/auth/validate?token=YOUR_TOKEN_HERE"
```

### Frontend Testing Interface

A complete web-based testing interface is available in the `frontend/` directory:

```bash
# Navigate to frontend directory
cd frontend/

# Open index.html in your browser
# Or serve with a simple HTTP server
python -m http.server 8000
# Then visit: http://localhost:8000
```

**Features:**
- 🟢 **Health Check**: Test API connectivity
- 🔐 **User Registration**: Create new accounts
- 🔑 **User Login**: Authenticate and get JWT tokens
- ✅ **Token Validation**: Verify JWT token validity
- 🎨 **Responsive Design**: Works on desktop and mobile
- 🟨 **Visual Feedback**: Color-coded success/error messages

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

The schema includes test users for development:

- **admin**: password `[BCRYPT_HASHED]` 
- **testuser**: password `[BCRYPT_HASHED]`

**Note**: Test users are automatically created via `schema.sql`

## 📊 Database Schema

### Credentials Table
```sql
CREATE TABLE credentials (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);
```

**Simple and Efficient**: Direct JDBC implementation with minimal schema for optimal performance.


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

### Version 0.0.1-SNAPSHOT (Current)
- ✅ **JWT Authentication**: Secure token-based auth with refresh tokens
- ✅ **JDBC Integration**: Direct JDBC for optimal performance (replaced JPA)
- ✅ **Docker MySQL Setup**: Containerized database with automated schema
- ✅ **Hamachi VPN Support**: Secure team collaboration setup
- ✅ **Frontend Testing Interface**: Complete web-based API testing
- ✅ **Team Documentation**: VPN setup guides and checklists
- ✅ **BCrypt Security**: Password hashing with strength 12
- ✅ **Input Validation**: Comprehensive request validation
- ✅ **Global Exception Handling**: Structured error responses
- ✅ **Modern Spring Security**: Latest security configuration
- ✅ **Docker Support**: Complete containerization setup
