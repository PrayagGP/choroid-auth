# Choroid Authentication System - Complete JWT Flow

A complete JWT-based authentication system with automatic login and seamless user experience.

## 🎯 **Authentication Flow**

### **Main Entry Point: `app.html`**
- **Purpose**: Main entry point that automatically checks authentication status
- **Behavior**: 
  - Checks if user has valid JWT token in localStorage
  - Validates token with backend server
  - Redirects to dashboard if valid token exists
  - Redirects to login if no valid token

### **Login Flow**
1. **User visits `app.html`** (main entry point)
2. **No valid token found** → Redirects to `login.html`
3. **User logs in** → Receives JWT tokens
4. **Tokens stored** in localStorage
5. **Redirects to dashboard** → `dashboard.html`

### **Auto-Login Flow**
1. **User visits `app.html`** (main entry point)
2. **Valid token found** in localStorage
3. **Token validated** with backend
4. **Auto-redirects to dashboard** → No login needed!

## 📁 **File Structure**

```
frontend/
├── app.html              # Main entry point (JWT check & redirect)
├── login.html            # Login page with form
├── signup.html           # User registration page
├── dashboard.html        # Protected landing page for authenticated users
├── index.html            # API testing interface (existing)
└── start-test-environment.bat  # Launch script
```

## 🚀 **How to Use**

### **Method 1: Direct Access (Recommended)**
```bash
# Open the main entry point
open frontend/app.html
# or
start frontend/app.html
```

### **Method 2: Complete Environment**
```bash
# Run the launcher (starts backend + frontend)
frontend/start-test-environment.bat
```

## 🔄 **User Journey**

### **First Time User**
1. Opens `app.html` → No token found
2. Redirected to `login.html`
3. Clicks "Sign up here" → Goes to `signup.html`
4. Creates account → Success message
5. Redirected back to `login.html`
6. Logs in → JWT tokens received
7. Redirected to `dashboard.html` → Authenticated!

### **Returning User**
1. Opens `app.html` → Token found and valid
2. **Automatically redirected** to `dashboard.html`
3. **No login required** → Seamless experience!

### **Expired Token**
1. Opens `app.html` → Token found but expired
2. Token validation fails
3. localStorage cleared
4. Redirected to `login.html` → Must log in again

## ✨ **Key Features**

### **🔐 Automatic Authentication**
- **Smart Token Check**: Validates JWT tokens on every visit
- **Seamless Auto-Login**: No need to login again if token is valid
- **Graceful Expiration**: Handles expired tokens cleanly

### **💾 Persistent Sessions**
- **localStorage Storage**: JWT tokens persist across browser sessions
- **Automatic Cleanup**: Invalid/expired tokens automatically removed
- **Multi-tab Support**: Authentication state shared across tabs

### **🎨 Modern UI/UX**
- **Loading States**: Visual feedback during authentication checks
- **Smooth Transitions**: Professional page transitions
- **Responsive Design**: Works on all devices
- **Error Handling**: Clear error messages and fallbacks

### **🔒 Security Features**
- **Server-side Validation**: All tokens validated with backend
- **Automatic Logout**: Invalid sessions automatically logged out
- **Secure Storage**: Tokens stored in browser localStorage
- **CSRF Protection**: No vulnerable cookies used

## 📋 **Page Details**

### **`app.html` - Authentication Router**
- **Purpose**: Main entry point that determines where to send user
- **Logic**: 
  ```
  Token exists? → Validate with server
    ├─ Valid: → dashboard.html
    └─ Invalid: → login.html
  No token? → login.html
  ```

### **`login.html` - Authentication**
- **Features**: 
  - Modern login form with validation
  - Quick login buttons for demo users
  - Loading states and error handling
  - Auto-redirect after successful login
  - Link to signup page

### **`signup.html` - User Registration**
- **Features**:
  - User registration with validation
  - Password confirmation
  - Real-time validation feedback
  - Auto-redirect to login after signup
  - Duplicate username detection

### **`dashboard.html` - Protected Landing Page**
- **Features**:
  - Welcome message with user info
  - JWT token display and management
  - Authentication status indicators
  - Token validation tools
  - Logout functionality
  - Links to API testing tools

## 🧪 **Testing the Flow**

### **Test Scenarios**

1. **New User Journey**
   ```
   app.html → login.html → signup.html → login.html → dashboard.html
   ```

2. **Existing User (First Login)**
   ```
   app.html → login.html → dashboard.html
   ```

3. **Returning User (Auto-Login)**
   ```
   app.html → dashboard.html (automatic!)
   ```

4. **Expired Session**
   ```
   app.html → login.html (token expired)
   ```

### **Demo Credentials**
- **Username**: `admin` | **Password**: `[ADMIN_PASSWORD]`
- **Username**: `testuser` | **Password**: `[TEST_PASSWORD]`

## 🔧 **Configuration**

### **Backend Requirements**
- Service running on `http://localhost:8080`
- JWT validation endpoint: `/api/auth/validate`
- Login endpoint: `/api/auth/login`
- Signup endpoint: `/api/auth/signup`

### **Token Management**
- **Access Token**: 24-hour expiration (configurable)
- **Refresh Token**: 7-day expiration (configurable)
- **Storage**: Browser localStorage
- **Validation**: Server-side verification required

## 🎭 **User Experience**

### **Benefits**
- **No Repeated Logins**: Users stay logged in until token expires
- **Fast Access**: Returning users go straight to dashboard
- **Clear Navigation**: Always know where you are in the flow
- **Professional Feel**: Modern UI with smooth transitions

### **Error Handling**
- **Network Errors**: Graceful fallbacks when service is down
- **Invalid Tokens**: Automatic cleanup and re-authentication
- **Validation Errors**: Clear, actionable error messages
- **Timeout Handling**: Proper handling of slow network connections

## 📱 **Browser Support**

- ✅ Chrome 90+
- ✅ Firefox 88+
- ✅ Safari 14+
- ✅ Edge 90+
- ✅ Mobile browsers

## 🚀 **Getting Started**

1. **Start the backend service**:
   ```bash
   ./gradlew bootRun
   ```

2. **Open the main entry point**:
   ```bash
   start frontend/app.html
   ```

3. **Experience the flow**:
   - First visit: Will go to login
   - Create account or use demo credentials
   - Future visits: Auto-login to dashboard!

**Your complete JWT authentication system is ready!** 🎉

The system now provides a seamless, professional authentication experience with automatic login and proper token management.
