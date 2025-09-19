# 🐳 Docker MySQL Database Setup Guide

## 📋 **Current Configuration**

Your **Choroid Authentication Service** is now configured to use a Docker MySQL container that can be accessed remotely!

### 🔍 **Database Details**
- **Container Name**: `my-mysql-db`
- **Database Name**: `choroid_db`
- **Username**: `root`
- **Password**: `apdddbs19`
- **Local Port**: `3307` (mapped from container's 3306)
- **Container IP**: `172.17.0.2`

### 🌐 **Network Access**
- **Your WiFi IP**: `192.168.1.36`
- **Local Access**: `localhost:3307`
- **Remote Access**: `192.168.1.36:3307`

## 🚀 **For You (Local Development)**

### **Application Configuration**
The `application.properties` has been updated to:
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/choroid_db
spring.datasource.username=root
spring.datasource.password=apdddbs19
```

### **Running Your Application**
```bash
# Start your Spring Boot app
./gradlew bootRun

# Or using environment variables for different configs
DB_URL=jdbc:mysql://localhost:3307/choroid_db ./gradlew bootRun
```

### **MySQL Workbench Connection**
- **Host**: `localhost`
- **Port**: `3307`
- **Username**: `root`
- **Password**: `apdddbs19`
- **Database**: `choroid_db`

## 👥 **For Your Friends (Remote Access)**

### **Prerequisites**
1. **Java 24** installed
2. **Git** for cloning the repository
3. **Network access** to your machine (`192.168.1.36`)

### **Setup Steps**

#### 1. **Clone Your Repository**
```bash
git clone https://github.com/PrayagGP/choroid-auth.git
cd choroid-auth
git checkout feature/jpa-to-jdbc-conversion
```

#### 2. **Configure for Remote Database**
Create a file called `application-remote.properties`:
```properties
# Database Configuration - Remote Docker MySQL
spring.datasource.url=jdbc:mysql://192.168.1.36:3307/choroid_db
spring.datasource.username=root
spring.datasource.password=apdddbs19
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JWT Configuration
jwt.secret=your-256-bit-secret-key-here-change-this-in-production
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# Server Configuration (different port to avoid conflicts)
server.port=8081

# Logging Configuration
logging.level.com.ddbs.choroid_auth_service=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

#### 3. **Run with Remote Profile**
```bash
# Using environment variables
export DB_URL=jdbc:mysql://192.168.1.36:3307/choroid_db
export DB_USERNAME=root
export DB_PASSWORD=apdddbs19
export SERVER_PORT=8081

./gradlew bootRun
```

**Or using the remote profile:**
```bash
./gradlew bootRun --args='--spring.profiles.active=remote'
```

### **Alternative: Docker Compose for Friends**
Create a `docker-compose.yml` for easy setup:
```yaml
version: '3.8'
services:
  choroid-auth:
    build: .
    ports:
      - "8081:8080"
    environment:
      - DB_URL=jdbc:mysql://192.168.1.36:3307/choroid_db
      - DB_USERNAME=root
      - DB_PASSWORD=apdddbs19
      - SERVER_PORT=8080
    depends_on:
      - mysql
  
  mysql:
    image: mysql:8.0
    container_name: remote-mysql-proxy
    ports:
      - "3308:3306"
    environment:
      MYSQL_ROOT_PASSWORD: apdddbs19
      MYSQL_DATABASE: choroid_db
    command: >
      --default-authentication-plugin=mysql_native_password
      --bind-address=0.0.0.0
```

## 🔧 **Network & Firewall Configuration**

### **Windows Firewall (Your Machine)**
You may need to allow incoming connections on port 3307:

```powershell
# Run as Administrator
New-NetFirewallRule -DisplayName "MySQL Docker Container" -Direction Inbound -Protocol TCP -LocalPort 3307 -Action Allow
```

### **Router Configuration**
If friends are accessing from different networks, you might need to:
1. **Port Forward** `3307` to your machine (`192.168.1.36`)
2. **Use your public IP** instead of local IP
3. **Configure Dynamic DNS** if your IP changes

## 🧪 **Testing Remote Access**

### **From Your Machine**
```bash
# Test database connection
mysql -h localhost -P 3307 -u root -papdddbs19 choroid_db -e "SELECT 'Connection successful' as Status;"

# Test application
curl http://localhost:8080/api/auth/health
```

### **From Friend's Machine**
```bash
# Test database connection
mysql -h 192.168.1.36 -P 3307 -u root -papdddbs19 choroid_db -e "SELECT 'Remote connection successful' as Status;"

# Test application (if they're running it locally connecting to your DB)
curl http://localhost:8081/api/auth/health
```

## 📊 **Database Management**

### **Viewing Data**
```bash
# Connect to container
docker exec -it my-mysql-db mysql -uroot -papdddbs19 choroid_db

# Check credentials table
SELECT username, LEFT(password, 20) as password_preview FROM credentials;

# Add new user
INSERT INTO credentials (username, password) VALUES 
('friend1', '$2a$12$5Xh8YWu9sFvVc/rSvSs3aOMfxxooGLrt80EBrGFNMhaXVAIk9PaZK');
```

### **Backup & Restore**
```bash
# Backup
docker exec my-mysql-db mysqldump -uroot -papdddbs19 choroid_db > choroid_backup.sql

# Restore
docker exec -i my-mysql-db mysql -uroot -papdddbs19 choroid_db < choroid_backup.sql
```

## 🔒 **Security Considerations**

### **Production Setup**
- **Change default password**: `apdddbs19` is not secure for production
- **Create specific users**: Don't use root for applications
- **Enable SSL**: Secure connections between app and database
- **Firewall rules**: Only allow necessary IPs
- **Use environment variables**: Don't commit passwords to Git

### **Create Application User**
```sql
CREATE USER 'choroid_app'@'%' IDENTIFIED BY 'SecurePassword123!';
GRANT SELECT, INSERT, UPDATE, DELETE ON choroid_db.* TO 'choroid_app'@'%';
FLUSH PRIVILEGES;
```

## 🚀 **Quick Start Commands**

### **Start Everything**
```bash
# 1. Start Docker MySQL (if not running)
docker start my-mysql-db

# 2. Start your application
./gradlew bootRun

# 3. Test endpoints
curl http://localhost:8080/api/auth/health
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### **For Friends**
```bash
# 1. Set environment variables
export DB_URL=jdbc:mysql://192.168.1.36:3307/choroid_db
export DB_USERNAME=root
export DB_PASSWORD=apdddbs19
export SERVER_PORT=8081

# 2. Run application
./gradlew bootRun

# 3. Test
curl http://localhost:8081/api/auth/health
```

## 📞 **Troubleshooting**

### **Common Issues**
1. **Connection Refused**: Check if Docker container is running
2. **Access Denied**: Verify username/password
3. **Network Timeout**: Check firewall and network connectivity
4. **Port Conflicts**: Use different ports for each instance

### **Debug Commands**
```bash
# Check Docker container status
docker ps -a
docker logs my-mysql-db

# Test network connectivity
telnet 192.168.1.36 3307
ping 192.168.1.36

# Check application logs
./gradlew bootRun --debug
```

## ✅ **Success Verification**

When everything is working, you should see:
1. ✅ Docker container running on port 3307
2. ✅ Database `choroid_db` with `credentials` table
3. ✅ Spring Boot app connecting successfully
4. ✅ Friends can connect remotely to your database
5. ✅ Authentication endpoints working for everyone

**Happy collaborative development! 🎉**