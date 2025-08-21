@echo off
REM Comprehensive Docker Fix Script
echo ========================================
echo Docker Desktop Fix Script
echo ========================================

echo Step 1: Stopping Docker Desktop...
taskkill /F /IM "Docker Desktop.exe" >nul 2>&1
timeout /t 5 /nobreak >nul

echo Step 2: Stopping Docker services...
net stop "com.docker.service" >nul 2>&1
timeout /t 3 /nobreak >nul

echo Step 3: Starting Docker Desktop...
start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"

echo Step 4: Waiting for Docker Desktop to start...
echo This may take 2-3 minutes...

set /a counter=0
:wait_loop
timeout /t 15 /nobreak >nul
set /a counter+=1

REM Check if Docker Desktop process is running
tasklist /FI "IMAGENAME eq Docker Desktop.exe" 2>NUL | find /I /N "Docker Desktop.exe">NUL
if not "%ERRORLEVEL%"=="0" (
    if %counter% geq 8 (
        echo ❌ Docker Desktop failed to start
        goto :manual_fix
    )
    echo Docker Desktop starting... (%counter%/8)
    goto :wait_loop
)

echo ✅ Docker Desktop process is running

echo Step 5: Waiting for Docker daemon...
set /a daemon_counter=0
:daemon_wait
timeout /t 10 /nobreak >nul
set /a daemon_counter+=1

docker version >nul 2>&1
if not errorlevel 1 (
    echo ✅ Docker daemon is ready!
    goto :success
)

if %daemon_counter% geq 12 (
    echo ❌ Docker daemon did not start within 2 minutes
    goto :manual_fix
)

echo Docker daemon starting... (%daemon_counter%/12)
goto :daemon_wait

:success
echo.
echo ========================================
echo ✅ SUCCESS! Docker is now working
echo ========================================
echo.
echo Docker version:
docker version --format "Client: {{.Client.Version}}, Server: {{.Server.Version}}"
echo.
echo You can now run your Docker commands:
echo docker build -t ruffes/billing-app:latest .
echo ========================================
goto :end

:manual_fix
echo.
echo ========================================
echo ❌ Automatic fix failed
echo ========================================
echo.
echo Please try these manual steps:
echo.
echo 1. RESTART YOUR COMPUTER
echo    - This often resolves Docker connection issues
echo.
echo 2. CHECK DOCKER DESKTOP INSTALLATION:
echo    - Uninstall Docker Desktop
echo    - Download latest version from docker.com
echo    - Install as Administrator
echo.
echo 3. CHECK WINDOWS FEATURES:
echo    - Open "Turn Windows features on or off"
echo    - Enable: Hyper-V, Windows Subsystem for Linux
echo    - Restart computer
echo.
echo 4. CHECK WSL2 (if using WSL2 backend):
echo    - Run: wsl --update
echo    - Run: wsl --shutdown
echo    - Restart Docker Desktop
echo.
echo 5. SWITCH DOCKER BACKEND:
echo    - Open Docker Desktop Settings
echo    - Go to General
echo    - Try switching between WSL2 and Hyper-V
echo ========================================

:end
pause
