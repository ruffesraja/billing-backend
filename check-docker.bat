@echo off
REM Docker Troubleshooting Script
echo ========================================
echo Docker Desktop Troubleshooting
echo ========================================

echo 1. Checking if Docker Desktop is running...
tasklist /FI "IMAGENAME eq Docker Desktop.exe" 2>NUL | find /I /N "Docker Desktop.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo ✅ Docker Desktop process is running
) else (
    echo ❌ Docker Desktop process is NOT running
    echo Please start Docker Desktop and wait for it to fully load
    goto :end
)

echo.
echo 2. Checking Docker daemon connection...
docker version >nul 2>&1
if errorlevel 1 (
    echo ❌ Cannot connect to Docker daemon
    echo Docker Desktop may still be starting up...
) else (
    echo ✅ Docker daemon is accessible
    docker version
    goto :success
)

echo.
echo 3. Checking Docker Desktop services...
sc query "com.docker.service" >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker Desktop service is not running
) else (
    echo ✅ Docker Desktop service is running
)

echo.
echo 4. Waiting for Docker to be ready...
echo This may take a few minutes...
set /a counter=0
:wait_loop
timeout /t 10 /nobreak >nul
set /a counter+=1
docker version >nul 2>&1
if not errorlevel 1 (
    echo ✅ Docker is now ready!
    goto :success
)
if %counter% geq 12 (
    echo ❌ Docker did not start within 2 minutes
    goto :troubleshoot
)
echo Waiting... (%counter%/12)
goto :wait_loop

:success
echo.
echo ========================================
echo ✅ Docker is working correctly!
echo ========================================
echo You can now run your Docker commands.
goto :end

:troubleshoot
echo.
echo ========================================
echo ❌ Docker Troubleshooting Required
echo ========================================
echo.
echo Please try the following steps:
echo 1. Close Docker Desktop completely
echo 2. Restart Docker Desktop as Administrator
echo 3. Wait for Docker Desktop to show "Engine running" status
echo 4. Try running this script again
echo.
echo If the problem persists:
echo 1. Restart your computer
echo 2. Reinstall Docker Desktop
echo 3. Check Windows features (Hyper-V, WSL2)
echo ========================================

:end
pause
