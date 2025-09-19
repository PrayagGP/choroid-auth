# Restart MySQL container with VPN-friendly configuration
Write-Host "🔧 Restarting MySQL container for VPN access..." -ForegroundColor Green

# Stop existing container
Write-Host "Stopping existing MySQL container..." -ForegroundColor Yellow
docker stop my-mysql-db

# Remove existing container (keep data volume)
Write-Host "Removing container (keeping data)..." -ForegroundColor Yellow
docker rm my-mysql-db

# Start new container with VPN-friendly settings
Write-Host "Starting MySQL with VPN configuration..." -ForegroundColor Yellow
docker run -d `
  --name my-mysql-db `
  -e MYSQL_ROOT_PASSWORD=apdddbs19 `
  -e MYSQL_DATABASE=choroid_db `
  -e MYSQL_USER=choroid_user `
  -e MYSQL_PASSWORD=choroid_pass `
  -p 3307:3306 `
  -v mysql_data:/var/lib/mysql `
  -v "${PWD}/src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql" `
  mysql:8.0 `
  --bind-address=0.0.0.0 `
  --default-authentication-plugin=mysql_native_password

# Wait for container to start
Write-Host "Waiting for MySQL to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Check status
Write-Host "Checking MySQL container status..." -ForegroundColor Green
docker ps --filter "name=my-mysql-db" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

Write-Host "✅ MySQL is ready for VPN connections!" -ForegroundColor Green
Write-Host "📋 Next steps:" -ForegroundColor Cyan
Write-Host "1. Install Hamachi VPN from https://www.vpn.net/" -ForegroundColor White
Write-Host "2. Create network: choroid-dev-team" -ForegroundColor White
Write-Host "3. Share your Hamachi IP with team members" -ForegroundColor White
Write-Host "4. Team members use your Hamachi IP to connect" -ForegroundColor White