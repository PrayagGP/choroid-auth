# 🛡️ Choroid Auth Service - VPN Setup Guide

## For Team Members (Remote Access)

### Step 1: Install Hamachi VPN
1. Download Hamachi from: https://www.vpn.net/
2. Install with administrator privileges
3. Create a LogMeIn account (free)
4. Launch Hamachi

### Step 2: Join the Development Network
1. In Hamachi, click **"Join an existing network"**
2. Network ID: `choroid-ddbs-team`
3. Password: `[ASK HOST FOR PASSWORD]`
4. Click **Join**

### Step 3: Get Your Hamachi IP
- Once connected, note your Hamachi IP address (usually starts with 25.x.x.x)
- You should see the host machine in your network list

### Step 4: Configure Your Application
Update your environment variables or application.properties:

```properties
# Use the HOST's Hamachi IP address
spring.datasource.url=jdbc:mysql://25.36.98.227:3307/choroid_db
spring.datasource.username=root
spring.datasource.password=apdddbs19

# Your application port (choose different ports for each team member)
server.port=8082  # or 8083, 8084, etc.
```

### Step 5: Test Connection
Run your Spring Boot application:
```bash
./gradlew bootRun
```

---

## For Host (Server Setup)

### Step 1: Install and Configure Hamachi
1. Download and install Hamachi
2. Create network: `choroid-ddbs-team`
3. Set a strong password
4. Share network details with team

### Step 2: Configure MySQL for VPN Access
Your MySQL is already configured to accept connections from any IP (0.0.0.0).

### Step 3: Share Your Hamachi IP
- Open Hamachi and note your IP address
- Share this IP with your team members
- They will use this IP to connect to your MySQL database

### Step 4: Firewall Configuration (If Needed)
If team members can't connect, you may need to allow Hamachi through Windows Firewall:
1. Open Windows Defender Firewall
2. Click "Allow an app or feature through Windows Defender Firewall"
3. Find "LogMeIn Hamachi" and ensure both Private and Public are checked
4. Click OK

---

## Connection Details Summary

| Component | Value |
|-----------|-------|
| Database Host | `25.36.98.227` (Host's Hamachi IP) |
| Database Port | `3307` |
| Database Name | `choroid_db` |
| Username | `root` |
| Password | `apdddbs19` |
| App Ports | 8081 (host), 8082+ (team members) |

---

## Testing the Connection

### Test MySQL Connection:
```bash
# From team member's machine
mysql -h 25.36.98.227 -P 3307 -u root -p
```

### Test API Endpoints:
```bash
# Health check
curl http://25.36.98.227:8081/api/auth/health

# Signup test (create a new user first)
curl -X POST http://25.36.98.227:8081/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass123"}'

# Login test
curl -X POST http://25.36.98.227:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass123"}'
```

---

## Troubleshooting

### Common Issues:
1. **Connection Refused**: Check if Hamachi is connected and green
2. **Database Connection Failed**: Verify MySQL container is running on host
3. **Timeout**: Check Windows Firewall settings
4. **Different Subnet**: Ensure both machines are in the same Hamachi network

### Host Commands to Check:
```bash
# Check MySQL container
docker ps -a --filter "name=mysql"

# Check Hamachi connection
ipconfig | findstr "25."
```

### Network Testing:
```bash
# Ping host from team member
ping 25.36.98.227

# Test MySQL port
telnet 25.36.98.227 3307
```

---

## Security Notes
- VPN provides secure encrypted tunnel
- Only team members with network credentials can access
- Database is not exposed to the public internet
- Use strong passwords for both VPN network and database