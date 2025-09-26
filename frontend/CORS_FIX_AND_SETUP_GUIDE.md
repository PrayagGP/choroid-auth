# 🔧 CORS Fix and Distributed Authentication System Setup Guide

## 📋 Overview

This guide documents the **CORS (Cross-Origin Resource Sharing)** issues encountered when connecting a frontend to a distributed authentication system via an API Gateway, and provides the complete solution including a local CORS proxy server.

## 🏗️ System Architecture

```
Frontend (Browser - http://localhost:3000)
    ↓ HTTP Requests with CORS headers
Local CORS Proxy (Node.js - http://localhost:3001)
    ↓ Cleaned HTTP requests (mimics curl)
Friend's API Gateway (http://25.7.141.58:8080) [via Hamachi VPN]
    ↓ Routes to auth-service
Your Backend (Spring Boot - 25.36.98.227:8080) [via Hamachi VPN]
    ↓ Database operations
MySQL Database (Docker - localhost:3307)
```

---

## 🚨 CORS Problem Analysis

### Initial Issues Encountered:

1. **Protocol Mismatch**: Frontend served from `file://` trying to access `http://` endpoints
2. **Browser Security**: Modern browsers block cross-origin requests without proper CORS headers
3. **API Gateway Restrictions**: Friend's gateway rejecting requests with browser-specific headers
4. **Mixed Content Policy**: HTTPS/HTTP protocol conflicts

### Error Symptoms:
```javascript
// Common CORS errors encountered:
{
  "error": "Failed to fetch",
  "type": "TypeError"
}

// API Gateway 403 responses:
{
  "status": 403,
  "statusText": "Forbidden"
}
```

---

## 🛠️ CORS Solution Implementation

### 1. **Backend CORS Configuration (SecurityConfig.java)**

Updated Spring Boot CORS settings to be more permissive:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Allow all origins for API gateway compatibility
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedOrigins(List.of("*"));
    
    // Allow all HTTP methods
    configuration.setAllowedMethods(List.of(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
    ));
    
    // Allow all headers
    configuration.setAllowedHeaders(List.of("*"));
    
    // Disable credentials for gateway compatibility
    configuration.setAllowCredentials(false);
    
    // Expose necessary headers
    configuration.setExposedHeaders(List.of(
        "Authorization", "Content-Type", "X-Requested-With", 
        "Accept", "Origin", "Access-Control-Request-Method", 
        "Access-Control-Request-Headers", "Access-Control-Allow-Origin"
    ));
    
    // Set max age for preflight requests
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    
    return source;
}
```

### 2. **Local CORS Proxy Server (proxy-server.js)**

Created a Node.js proxy server to handle CORS and strip problematic browser headers:

```javascript
const http = require('http');
const url = require('url');

const PORT = 3001;
const TARGET_HOST = '25.7.141.58:8080';

const server = http.createServer((req, res) => {
    // Set CORS headers for browser compatibility
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization, X-Requested-With');
    
    // Handle preflight OPTIONS request
    if (req.method === 'OPTIONS') {
        res.writeHead(200);
        res.end();
        return;
    }
    
    const targetUrl = `http://${TARGET_HOST}${url.parse(req.url).path}`;
    
    const proxyOptions = {
        method: req.method,
        headers: {
            ...req.headers,
            host: TARGET_HOST
        }
    };
    
    // Remove hop-by-hop headers and problematic browser headers
    delete proxyOptions.headers['connection'];
    delete proxyOptions.headers['upgrade'];
    delete proxyOptions.headers['http2-settings'];
    delete proxyOptions.headers['te'];
    delete proxyOptions.headers['trailer'];
    delete proxyOptions.headers['proxy-authorization'];
    delete proxyOptions.headers['proxy-authenticate'];
    delete proxyOptions.headers['proxy-connection'];
    
    // CRITICAL: Remove browser-specific headers that cause 403
    delete proxyOptions.headers['origin'];
    delete proxyOptions.headers['referer'];
    delete proxyOptions.headers['user-agent'];
    delete proxyOptions.headers['accept-encoding'];
    delete proxyOptions.headers['accept-language'];
    delete proxyOptions.headers['sec-fetch-dest'];
    delete proxyOptions.headers['sec-fetch-mode'];
    delete proxyOptions.headers['sec-fetch-site'];
    delete proxyOptions.headers['sec-ch-ua'];
    delete proxyOptions.headers['sec-ch-ua-mobile'];
    delete proxyOptions.headers['sec-ch-ua-platform'];
    
    // Set simple headers like curl (this is the key!)
    proxyOptions.headers['user-agent'] = 'curl/7.68.0';
    proxyOptions.headers['accept'] = '*/*';
    
    // Create and execute proxy request
    const proxyReq = http.request(targetUrl, proxyOptions, (proxyRes) => {
        res.writeHead(proxyRes.statusCode, {
            ...proxyRes.headers,
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
            'Access-Control-Allow-Headers': 'Content-Type, Authorization, X-Requested-With'
        });
        proxyRes.pipe(res);
    });
    
    proxyReq.on('error', (err) => {
        res.writeHead(502, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            error: 'Bad Gateway',
            message: 'Failed to connect to target server',
            details: err.message
        }));
    });
    
    req.pipe(proxyReq);
});

server.listen(PORT, () => {
    console.log(`CORS Proxy server running on http://localhost:${PORT}`);
    console.log(`Proxying requests to: http://${TARGET_HOST}`);
});
```

### 3. **Frontend File Changes**

Updated all frontend files to use the local CORS proxy instead of direct API gateway access:

#### **Before (Direct Gateway Access):**
```javascript
const API_BASE_URL = 'http://25.7.141.58:8080/api/auth';
```

#### **After (Via CORS Proxy):**
```javascript
const API_BASE_URL = 'http://localhost:3001/api/auth';
```

#### **Files Modified:**
- `app.html` - Line 62
- `login.html` - Line 274  
- `signup.html` - Line 248
- `dashboard.html` - Line 390
- `index.html` - (testing interface)

---

## 🚀 Step-by-Step Setup Instructions

### **Prerequisites:**
- ✅ Java 24+ installed
- ✅ Node.js installed  
- ✅ Python 3 installed
- ✅ Docker installed
- ✅ Hamachi VPN connected to `choroid-ddbs-team` network

### **Step 1: Database Setup**
```bash
# Navigate to project root
cd C:\Users\HP\OneDrive\Documents\DDBS Project\choroid-auth-service\choroid-auth-service

# Start MySQL Docker container
docker-compose -f docker-compose-mysql.yml up -d

# Verify database is running
docker ps -a --filter "name=mysql"
```

**Expected Output:**
```
CONTAINER ID   IMAGE     COMMAND                  STATUS         PORTS                               NAMES
08d8faf00fda   mysql     "docker-entrypoint.s…"  Up X minutes   33060/tcp, 0.0.0.0:3307->3306/tcp   my-mysql-db
```

### **Step 2: Backend Service Startup**
```bash
# Build the Spring Boot application
.\gradlew build

# Start the backend service (in separate PowerShell window)
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; .\gradlew bootRun" -WindowStyle Normal
```

**Verification:**
```bash
# Wait 10-15 seconds for startup, then test
curl http://localhost:8080/api/auth/health
```

**Expected Response:**
```json
{
  "service": "Choroid Authentication Service",
  "status": "UP", 
  "timestamp": "2025-09-26T19:47:37.019645800"
}
```

### **Step 3: Network Connectivity Verification**
```bash
# Check your Hamachi IP
ipconfig | findstr "25."

# Test friend's API gateway connectivity
Test-NetConnection 25.7.141.58 -Port 8080

# Verify gateway routes to your backend
curl http://25.7.141.58:8080/api/auth/health
```

**Expected Results:**
- Your Hamachi IP: `25.36.98.227`
- TCP Test: `TcpTestSucceeded : True`
- Gateway Health: `200 OK` response

### **Step 4: CORS Proxy Server Setup**
```bash
# Navigate to frontend directory
cd frontend

# Start the CORS proxy server (in separate PowerShell window)
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; node proxy-server.js" -WindowStyle Normal
```

**Verification:**
```bash
# Test proxy is working
curl http://localhost:3001/api/auth/health
```

### **Step 5: Frontend HTTP Server**
```bash
# Start HTTP server for frontend (in separate PowerShell window)
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; python -m http.server 3000" -WindowStyle Minimized
```

**Verification:**
```bash
# Check HTTP server is running
netstat -ano | findstr :3000
```

---

## ✅ Pre-Frontend Checklist

Before accessing the frontend, ensure these **4 services are running**:

### **Service Status Check:**

#### **1. MySQL Database**
```bash
docker ps -a --filter "name=mysql"
# Status should be "Up X minutes"
```

#### **2. Spring Boot Backend**  
```bash
netstat -ano | findstr :8080
# Should show LISTENING on port 8080
curl http://localhost:8080/api/auth/health
# Should return 200 OK with service status
```

#### **3. CORS Proxy Server**
```bash
netstat -ano | findstr :3001  
# Should show LISTENING on port 3001
curl http://localhost:3001/api/auth/health
# Should return 200 OK (proxied from gateway)
```

#### **4. Frontend HTTP Server**
```bash
netstat -ano | findstr :3000
# Should show LISTENING on port 3000
```

---

## 🌐 Frontend Access URLs

Once all services are running, access the frontend via:

### **Main Application Flow:**
1. **Entry Point**: `http://localhost:3000/app.html`
2. **Login Page**: `http://localhost:3000/login.html`  
3. **Signup Page**: `http://localhost:3000/signup.html`
4. **Dashboard**: `http://localhost:3000/dashboard.html`

### **Testing/Debug Pages:**
- **API Testing**: `http://localhost:3000/index.html`
- **Proxy Debug**: `http://localhost:3000/proxy_test.html`
- **Direct Debug**: `http://localhost:3000/debug_test.html`

---

## 🧪 Testing the Complete System

### **Test User Credentials:**
- **Existing User**: `newuser456` / `testpass123`
- **Create New**: Use signup form to register

### **Test Flow:**
1. **Open**: `http://localhost:3000/app.html`
2. **First Visit**: Redirects to login (no stored token)
3. **Login**: Enter credentials → JWT token generated
4. **Dashboard**: Automatic redirect after successful login
5. **Refresh**: Should stay logged in (token validation)
6. **Logout**: Clears tokens and redirects to login

### **Expected Network Flow:**
```
Browser Request → localhost:3000 (Frontend)
    ↓
Frontend JS → localhost:3001/api/auth/* (CORS Proxy)
    ↓  
Proxy → 25.7.141.58:8080/api/auth/* (Friend's Gateway)
    ↓
Gateway → 25.36.98.227:8080/api/auth/* (Your Backend)
    ↓
Backend → localhost:3307/choroid_db (MySQL)
```

---

## 🔍 Troubleshooting

### **Common Issues:**

#### **1. Port Already in Use**
```bash
# Find and kill conflicting process
netstat -ano | findstr :8080
taskkill /PID [PID_NUMBER] /F
```

#### **2. Hamachi Connection Issues**
```bash
# Check Hamachi status
ipconfig | findstr "25."
ping 25.36.98.227  # Your own IP should work
```

#### **3. CORS Errors Still Occurring**
- Verify proxy server is running on port 3001
- Check frontend files use `localhost:3001` not direct gateway
- Ensure browser is accessing `http://localhost:3000` not `file://`

#### **4. 403 Forbidden from Gateway**
- Verify proxy-server.js includes header stripping logic
- Check friend's gateway logs for filtering rules
- Test direct curl works: `curl http://25.7.141.58:8080/api/auth/health`

#### **5. Database Connection Errors**
```bash
# Check Docker MySQL is running
docker logs my-mysql-db
# Restart if needed
docker-compose -f docker-compose-mysql.yml restart
```

---

## 📊 Success Indicators

### **Gateway Logs (Friend's Side):**
```
Route matched: auth-service
PooledConnectionProvider: Multiple active connections  
HTTP/1.1 responses: 200 OK
Token validation: Success
```

### **Browser Network Tab:**
- Requests to `localhost:3001` return 200 OK
- CORS headers present in responses
- JWT tokens in response payloads

### **Backend Logs:**
```
JWT Utility initialized with expiration: 86400000 ms
Authentication successful for user: [username]
Login successful for username: [username]
```

---

## 🎯 Key Technical Insights

### **Why Direct CORS Configuration Wasn't Enough:**
1. **Protocol Mismatch**: `file://` → `http://` blocked by browsers
2. **Gateway Filtering**: API gateway rejected browser-fingerprint headers
3. **Complex Headers**: Modern browsers send 10+ security headers that confuse gateways

### **Why the Proxy Solution Works:**
1. **Protocol Consistency**: `http://` → `http://` → `http://`
2. **Header Sanitization**: Strips browser fingerprints, sends clean curl-like requests
3. **CORS Handling**: Adds proper CORS headers for browser consumption
4. **Transparency**: Gateway sees simple, curl-like requests it expects

### **Production Considerations:**
- Replace proxy with proper API gateway CORS configuration
- Use environment variables for API URLs  
- Implement proper SSL/TLS certificates
- Add request/response logging and monitoring

---

## 🏆 Final Architecture Benefits

✅ **Browser Compatibility**: Works with all modern browsers  
✅ **Security**: Multiple layers (VPN, JWT, CORS)  
✅ **Scalability**: Microservice architecture with gateway  
✅ **Maintainability**: Clear separation of concerns  
✅ **Performance**: Connection pooling and efficient routing  
✅ **Development Experience**: Easy local testing and debugging  

This solution demonstrates enterprise-level distributed system patterns while solving practical CORS challenges in a development environment.