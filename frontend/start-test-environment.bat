@echo off
echo Starting Choroid Authentication Service Test Environment...
echo.

echo 1. Starting Backend Service...
cd ..
start "Choroid Auth Service" cmd /k "gradlew bootRun"

echo 2. Waiting for service to start...
timeout /t 10 /nobreak

echo 3. Opening Frontend Test Interface...
start frontend\index.html

echo.
echo Test Environment Started!
echo - Backend: http://localhost:8080
echo - Frontend: opened in your default browser
echo.
echo Press any key to exit...
pause > nul
