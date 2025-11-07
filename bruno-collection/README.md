# Choroid Auth Service - Bruno API Testing Guide

This Bruno collection provides comprehensive testing for all backend APIs in the Choroid Authentication Service.

## 📋 Prerequisites

1. **Install Bruno**: Download from [usebruno.com](https://www.usebruno.com/)
2. **Start the service**: Ensure the Spring Boot application is running on `http://localhost:8081`
   ```bash
   ./gradlew bootRun
   ```
3. **Database**: MySQL should be running with the schema initialized

## 🚀 Getting Started

### 1. Open Collection in Bruno

1. Launch Bruno
2. Click "Open Collection"
3. Navigate to this directory: `bruno-collection`
4. Bruno will load all API requests

### 2. Set Up Environment

The collection includes a **Local** environment with these variables:
- `baseUrl`: http://localhost:8081 (change if using different port)
- `token`: Auto-populated after login
- `refreshToken`: Auto-populated after login  
- `username`: Auto-populated after login

To switch environments: Click environment dropdown → Select "Local"

## 📝 API Endpoints Overview

### 1. Health Check ✅
- **Method**: GET
- **Endpoint**: `/api/auth/health`
- **Auth**: None
- **Purpose**: Verify service is running

**Test Steps**:
1. Click on "1. Health Check"
2. Click "Send"
3. Expect 200 OK with status "UP"

---

### 2. User Signup 📝
- **Method**: POST
- **Endpoint**: `/api/auth/signup`
- **Auth**: None
- **Purpose**: Register new user

**Test Steps**:
1. Click on "2. User Signup"
2. Modify username in request body (must be unique)
3. Set password (minimum 6 characters)
4. Click "Send"
5. Expect 201 Created

**Request Body**:
```json
{
  "username": "testuser123",
  "password": "securePass123"
}
```

**Validation Rules**:
- Username: 3-50 characters, alphanumeric with `.`, `_`, `-`
- Password: 6-100 characters

**Common Errors**:
- 409 Conflict: Username already exists (choose different username)
- 400 Bad Request: Validation failed (check username/password format)

---

### 3. User Login 🔐
- **Method**: POST
- **Endpoint**: `/api/auth/login`
- **Auth**: None
- **Purpose**: Authenticate and receive JWT tokens

**Test Steps**:
1. Click on "3. User Login"
2. Use credentials from signup step
3. Click "Send"
4. Token automatically saved to environment (check Console tab)
5. Expect 200 OK

**Request Body**:
```json
{
  "username": "testuser123",
  "password": "securePass123"
}
```

**Response**:
```json
{
  "token": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "username": "testuser123",
  "issuedAt": "2025-01-12T12:00:00",
  "expiresAt": "2025-01-13T12:00:00",
  "message": "Login successful"
}
```

**Cookies Set** (for gateway integration):
- `jwtToken`: JWT access token (1 hour)
- `username`: Username

**Common Errors**:
- 401 Unauthorized: Invalid credentials

---

### 4. Validate Token ✔️
- **Method**: POST
- **Endpoint**: `/api/auth/validate?token={{token}}`
- **Auth**: None
- **Purpose**: Verify JWT token validity

**Test Steps**:
1. First complete "User Login" to get token
2. Click on "4. Validate Token"
3. Token from environment is used automatically
4. Click "Send"
5. Expect 200 OK with `"valid": true`

**Response** (Valid token):
```json
{
  "valid": true,
  "username": "testuser123",
  "message": "Token is valid"
}
```

**Response** (Invalid/Expired):
```json
{
  "valid": false,
  "username": null,
  "message": "Token is invalid or expired"
}
```

**Testing Scenarios**:
- ✅ Valid token: Use token from recent login
- ❌ Invalid token: Manually modify `{{token}}` in environment
- ⏰ Expired token: Wait 24 hours after login (or modify JWT expiration config)

---

### 5. Update Password 🔄
- **Method**: POST
- **Endpoint**: `/api/auth/update-password`
- **Auth**: None (password-based)
- **Purpose**: Change user password

**Test Steps**:
1. Click on "5. Update Password"
2. Set username, current password, new password, confirm password
3. Ensure newPassword and confirmPassword match
4. Click "Send"
5. Expect 200 OK

**Request Body**:
```json
{
  "username": "testuser123",
  "currentPassword": "securePass123",
  "newPassword": "newSecurePass456",
  "confirmPassword": "newSecurePass456"
}
```

**Success Response**:
```json
{
  "message": "Password updated successfully",
  "username": "testuser123",
  "timestamp": "2025-01-12T12:00:00"
}
```

**Common Errors**:
- 400 Bad Request: newPassword and confirmPassword don't match
- 401 Unauthorized: Wrong current password
- 400 Bad Request: Password doesn't meet validation rules

**After Password Update**:
- Login again with new password to get fresh tokens
- Old tokens remain valid until expiration

---

## 🔄 Complete Testing Workflow

Follow this sequence to test all APIs:

```
1. Health Check → Verify service is UP
2. User Signup → Create test account
3. User Login → Get JWT tokens (auto-saved)
4. Validate Token → Verify token is valid
5. Update Password → Change password
6. User Login (again) → Login with new password
7. Validate Token → Verify new token
```

## 🎯 Environment Variables

Bruno automatically manages these variables:

| Variable | Description | Auto-populated? |
|----------|-------------|-----------------|
| `baseUrl` | Service base URL | No (set to http://localhost:8081) |
| `token` | JWT access token | Yes (after login) |
| `refreshToken` | JWT refresh token | Yes (after login) |
| `username` | Logged in username | Yes (after login) |

**Viewing/Editing Environment Variables**:
1. Click the environment dropdown (top-right)
2. Select "Configure"
3. Edit values as needed

## 🧪 Testing Tips

### Test Different Scenarios

**Valid Requests**:
- Use proper format and valid credentials
- Verify success responses

**Invalid Requests**:
- Test with invalid username formats
- Test with short passwords (< 6 characters)
- Test with non-existent users
- Test with wrong passwords
- Test with expired tokens

### Common Testing Patterns

1. **Test Validation Errors**:
   ```json
   {
     "username": "ab",  // Too short
     "password": "123"   // Too short
   }
   ```

2. **Test Duplicate Signup**:
   - Signup with same username twice
   - Expect 409 Conflict on second attempt

3. **Test Invalid Login**:
   ```json
   {
     "username": "testuser123",
     "password": "wrongpassword"
   }
   ```

4. **Test Password Mismatch**:
   ```json
   {
     "newPassword": "newPass123",
     "confirmPassword": "differentPass456"
   }
   ```

## 🔧 Troubleshooting

### Service Not Responding
```bash
# Check if service is running
curl http://localhost:8081/api/auth/health

# Start the service
./gradlew bootRun
```

### Database Connection Issues
```bash
# Check MySQL container
docker ps

# Restart MySQL
docker-compose -f docker-compose-mysql.yml restart
```

### Port Already in Use
- Edit `application.properties` to change server port
- Update `baseUrl` in Bruno environment

### Token Not Saved
- Check Console tab in Bruno after login
- Verify post-response script executed
- Manually copy token to environment if needed

## 📊 Expected HTTP Status Codes

| Endpoint | Success | Error Codes |
|----------|---------|-------------|
| Health Check | 200 OK | - |
| Signup | 201 Created | 400 (Validation), 409 (User exists) |
| Login | 200 OK | 401 (Invalid credentials), 400 (Validation) |
| Validate Token | 200 OK | - (Returns valid: false) |
| Update Password | 200 OK | 400 (Mismatch/Validation), 401 (Wrong password) |

## 🔐 Security Notes

- **No Authentication Required**: All endpoints are public (password-based auth)
- **JWT Cookies**: Login sets cookies for gateway integration
- **Token Expiration**: Default 24 hours for access token
- **Password Hashing**: BCrypt with strength 12
- **HTTPS**: Configure for production (currently HTTP for development)

## 📚 Additional Resources

- **API Documentation**: See main [README.md](../README.md)
- **Curl Examples**: Check README for command-line testing
- **Web Interface**: http://localhost:8081/ (built-in test UI)
- **Schema**: `src/main/resources/schema.sql`

## 🎓 Learning Resources

**Bruno Features Used**:
- ✅ Environment variables
- ✅ Post-response scripts
- ✅ Query parameters
- ✅ JSON request bodies
- ✅ Inline documentation

**Next Steps**:
1. Test all endpoints in sequence
2. Try invalid scenarios
3. Test edge cases (special characters, max lengths)
4. Test with multiple users
5. Test token expiration behavior

---

**Happy Testing! 🚀**

For issues or questions, check the service logs:
```bash
./gradlew bootRun
# or
java -jar build/libs/choroid-auth-service-0.0.1-SNAPSHOT.jar
```
