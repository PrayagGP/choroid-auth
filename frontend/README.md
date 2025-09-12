# Choroid Authentication Service - Frontend Test Interface

A comprehensive web-based frontend for manually testing the Choroid Authentication Service API.

## 🚀 Features

### **Authentication Forms**
- **Login Form**: Test user authentication with existing credentials
- **Signup Form**: Register new users with validation
- **Input Validation**: Client-side validation with user-friendly error messages

### **Automated Testing**
- **Service Health Check**: Monitor backend service status
- **Token Validation**: Test JWT token validity
- **Error Scenario Testing**: Test invalid credentials and duplicate users
- **Quick Login Buttons**: One-click login for test users

### **Real-time Monitoring**
- **Response Log**: Live log of all API interactions
- **Token Display**: View and copy JWT access and refresh tokens
- **Status Indicators**: Visual service status (online/offline)
- **Timestamped Logging**: All activities logged with timestamps

### **Utilities**
- **Log Export**: Download test session logs
- **API Documentation**: Built-in API reference
- **Clear Functions**: Reset forms, tokens, and logs
- **Responsive Design**: Works on desktop and mobile

## 🛠️ Setup & Usage

### **1. Start the Backend Service**
```bash
# From the main project directory
./gradlew bootRun
```
Make sure the service is running on `http://localhost:8080`

### **2. Open the Frontend**
```bash
# Open the HTML file in your web browser
start frontend/index.html
```
Or simply double-click on `frontend/index.html`

### **3. Test the Service**
The frontend will automatically check service health when loaded.

## 📋 Testing Scenarios

### **✅ Successful Operations**

#### **User Registration**
1. Enter a new username (3-50 characters)
2. Enter a password (minimum 6 characters)
3. Click "Sign Up"
4. Verify success message and user creation

#### **User Login**
1. Enter existing username and password
2. Click "Login" 
3. Verify JWT tokens are returned and displayed
4. Check token expiration time

#### **Token Validation**
1. After successful login, click "Validate Current Token"
2. Verify token validity response
3. Check username extraction from token

### **❌ Error Scenarios**

#### **Invalid Login**
- **Test**: Click "Test Invalid Login" or enter wrong credentials
- **Expected**: 401 Unauthorized error
- **Result**: Error message displayed in log

#### **Duplicate User Registration**
- **Test**: Click "Test Duplicate Signup" or try to register existing user
- **Expected**: 409 Conflict error
- **Result**: "Username already exists" error message

#### **Service Unavailable**
- **Test**: Stop backend service and use frontend
- **Expected**: Connection errors and offline status
- **Result**: Service status shows offline, API calls fail

### **🔍 Validation Testing**

#### **Input Validation**
- **Username**: Must be 3-50 characters, alphanumeric + special chars
- **Password**: Must be minimum 6 characters
- **Form Validation**: Client-side validation prevents invalid submissions

#### **Security Testing**
- **Password Hashing**: Passwords are hashed on backend (BCrypt)
- **JWT Tokens**: Secure token generation and validation
- **CORS**: Cross-origin requests properly handled

## 🎯 Quick Test Users

If these users exist in your database, you can test with:

| Username | Password | Purpose |
|----------|----------|---------|
| `admin` | `admin123` | Administrator testing |
| `testuser` | `password123` | General user testing |

**Quick Login Buttons**: Use the purple and pink buttons for one-click testing.

## 📊 Response Log Features

### **Color-Coded Messages**
- 🔵 **Blue (Info)**: General information and API requests
- 🟢 **Green (Success)**: Successful operations and responses
- 🟡 **Yellow (Warning)**: Warnings and missing tokens
- 🔴 **Red (Error)**: Errors and failed operations

### **Detailed Logging**
- Timestamps for all operations
- Full JSON responses from API
- Request details and parameters
- Error messages and status codes

### **Export Functionality**
- Download complete test session log
- Formatted text file with all interactions
- Useful for debugging and documentation

## 🔧 Troubleshooting

### **Service Offline**
- Ensure backend is running on port 8080
- Check database connection
- Verify no firewall blocking

### **CORS Issues**
- Backend has CORS enabled for all origins
- If issues persist, check browser console

### **Token Issues**
- Tokens expire after 24 hours (default)
- Use "Validate Current Token" to check status
- Clear tokens and re-login if expired

### **Network Errors**
- Check backend service logs
- Ensure proper JSON formatting in requests
- Verify API endpoints are accessible

## 🎨 User Interface

### **Modern Design**
- Gradient backgrounds and modern card layouts
- Responsive design for all screen sizes
- Smooth animations and hover effects
- Professional color scheme

### **User Experience**
- Intuitive form layouts
- Clear visual feedback
- Easy-to-read response logs
- Quick access buttons for common tasks

### **Accessibility**
- Proper form labels and validation
- Keyboard navigation support
- Clear error messages
- Responsive font sizes

## 🔐 Security Notes

### **Development Only**
This frontend is designed for **development and testing purposes only**.

### **Production Considerations**
- Remove hardcoded test credentials
- Implement proper authentication for frontend
- Add rate limiting for production use
- Use HTTPS in production environment

### **Token Security**
- Tokens are stored in memory only
- Clear tokens when done testing
- Don't share tokens or logs with sensitive data

## 📱 Browser Compatibility

- ✅ Chrome 90+
- ✅ Firefox 88+
- ✅ Safari 14+
- ✅ Edge 90+

## 🤝 Usage Tips

1. **Start with Health Check**: Always verify service is online first
2. **Create Test Users**: Use signup to create test accounts
3. **Test Error Cases**: Use the quick test buttons for comprehensive testing
4. **Monitor Logs**: Watch the response log for detailed API interactions
5. **Export Logs**: Save test sessions for documentation
6. **Clear Regularly**: Clear logs and tokens between test sessions

---

**Happy Testing! 🎉**

This frontend provides everything you need to comprehensively test your authentication service manually.
