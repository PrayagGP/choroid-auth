# 🎯 Choroid Auth Service - Team Setup Checklist

## 📋 What to Share with Your Friends

### 1. **Hamachi VPN Credentials**
```
Network ID: choroid-ddbs-team
Password: [YOUR_CHOSEN_PASSWORD]
```
**Action:** Share these credentials securely (WhatsApp/Discord/etc.)

### 2. **Your Hamachi IP Address**
```
Host IP: 25.36.98.227
```
**Note:** This is your computer's VPN IP address

### 3. **Database Connection Details**
```
Database Host: 25.36.98.227
Database Port: 3307
Database Name: choroid_db
Database Username: root
Database Password: [PROVIDED_BY_HOST]
```

### 4. **Connection URL for Spring Boot**
```properties
spring.datasource.url=jdbc:mysql://25.36.98.227:3307/choroid_db
spring.datasource.username=root
spring.datasource.password=[PROVIDED_BY_HOST]
```

### 5. **API Base URL**
```
Base URL: http://25.36.98.227:8081/api/auth/
```

### 6. **Server Port Coordination**
```
Your server port: 8081 (already taken)
Available ports for team: 8082, 8083, 8084, 8085, etc.
```

## 📁 Files to Send

### **Essential File:**
- **`VPN_SETUP_GUIDE.md`** - Complete setup instructions

### **Optional Files:**
- **`docker-compose-mysql.yml`** - If they want to run their own MySQL
- **Entire `frontend/` folder** - For testing the API

## 🔧 Quick Setup Message Template

Here's a message you can copy-paste to your friends:

---

**Hey! Here are the details to connect to our Choroid Auth Service:**

**🛡️ VPN Setup:**
1. Download Hamachi: https://www.vpn.net/
2. Join network: `choroid-ddbs-team`
3. Password: `[YOUR_PASSWORD]`

**🔗 Connection Details:**
- My Hamachi IP: `25.36.98.227`
- Database URL: `jdbc:mysql://25.36.98.227:3307/choroid_db`
- Username: `root` | Password: `[PROVIDED_BY_HOST]`
- Use server port: `808X` (not 8081, I'm using that)

**📝 Test URLs:**
- Health: http://25.36.98.227:8081/api/auth/health
- API Base: http://25.36.98.227:8081/api/auth/

**📁 Files:** I'll send you the setup guide with detailed instructions.

Let me know when you're connected! 🚀

---

## ⚠️ Security Notes for Team

1. **VPN Password:** Choose a strong password and share it securely
2. **Database Access:** Only accessible through VPN - safe!
3. **Port Conflicts:** Each person uses a different server port
4. **Testing:** Everyone can test with the health endpoint first

## 🧪 Testing Steps for Team Members

### Step 1: Test VPN Connection
```bash
ping 25.36.98.227
```

### Step 2: Test API Access
```bash
curl http://25.36.98.227:8081/api/auth/health
```

### Step 3: Run Their Application
```bash
./gradlew bootRun
```
*Using their configured server port (8082+)*

## 🔧 Their Application.properties Should Look Like:
```properties
# Database (connect to your MySQL)
spring.datasource.url=jdbc:mysql://25.36.98.227:3307/choroid_db
spring.datasource.username=root
spring.datasource.password=[PROVIDED_BY_HOST]

# Their server port (different from yours)
server.port=8082  # or 8083, 8084, etc.

# Same JWT settings as yours
security.jwt.secret-key=[PROVIDED_BY_HOST]
security.jwt.access-token-expiration=86400000
security.jwt.refresh-token-expiration=604800000
```

## 🎯 Success Checklist

Your friends have successfully connected when:
- ✅ They can ping your Hamachi IP
- ✅ Health endpoint returns HTTP 200
- ✅ Their Spring Boot app starts without database errors
- ✅ They can create/login users through their app

---

**Ready to share! 🚀**