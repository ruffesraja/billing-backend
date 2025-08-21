@echo off
REM Check Docker Auto-Restart Configuration
echo ========================================
echo Docker Auto-Restart Configuration Check
echo ========================================

echo 1. Checking Docker Desktop startup configuration...
echo    Please verify in Docker Desktop Settings:
echo    General → "Start Docker Desktop when you log in" should be checked
echo.

echo 2. Checking Docker service startup type...
powershell -Command "Get-Service '*docker*' | Select-Object Name, StartType, Status | Format-Table -AutoSize"
echo.

echo 3. Checking current container restart policies...
docker inspect billing-app-docker --format="Container: {{.Name}} - Restart Policy: {{.HostConfig.RestartPolicy.Name}}" 2>nul
docker inspect billing-postgres-docker --format="Container: {{.Name}} - Restart Policy: {{.HostConfig.RestartPolicy.Name}}" 2>nul
echo.

echo 4. Checking current container status...
docker-compose ps 2>nul
echo.

echo 5. Summary of restart behavior:
echo    ✅ unless-stopped: Will restart after system reboot
echo    ✅ unless-stopped: Will restart after container crash  
echo    ❌ unless-stopped: Will NOT restart if manually stopped
echo.

echo ========================================
echo Auto-Restart Test Instructions:
echo ========================================
echo To test auto-restart after reboot:
echo 1. Start containers: docker-compose up -d
echo 2. Restart your computer
echo 3. After reboot, check: docker-compose ps
echo 4. Containers should be running automatically
echo ========================================

pause
