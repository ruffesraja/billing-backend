@echo off
REM Complete Build Workflow for Billing Application
REM This script: 1) Builds frontend, 2) Copies files, 3) Builds Docker image, 4) Pushes to Docker Hub
REM Usage: complete-build.bat [your-dockerhub-username] [tag-version]

setlocal enabledelayedexpansion

REM Configuration
set DEFAULT_USERNAME=your-dockerhub-username
set DEFAULT_TAG=latest
set IMAGE_NAME=billing-app

REM Get parameters or use defaults
if "%1"=="" (
    set /p DOCKER_USERNAME="Enter your Docker Hub username [%DEFAULT_USERNAME%]: "
    if "!DOCKER_USERNAME!"=="" set DOCKER_USERNAME=%DEFAULT_USERNAME%
) else (
    set DOCKER_USERNAME=%1
)

if "%2"=="" (
    set IMAGE_TAG=%DEFAULT_TAG%
) else (
    set IMAGE_TAG=%2
)

set FULL_IMAGE_NAME=%DOCKER_USERNAME%/%IMAGE_NAME%:%IMAGE_TAG%

echo ==========================================
echo Complete Billing Application Build
echo ==========================================
echo Docker Hub Username: %DOCKER_USERNAME%
echo Image Name: %IMAGE_NAME%
echo Tag: %IMAGE_TAG%
echo Full Image: %FULL_IMAGE_NAME%
echo ==========================================

REM Step 1: Check prerequisites
echo.
echo 🔍 Step 1: Checking prerequisites...

REM Check Docker
docker version >nul 2>&1
if errorlevel 1 (
    echo ❌ ERROR: Docker is not running or not installed!
    pause
    exit /b 1
)
echo ✅ Docker is running

REM Check Node.js
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ ERROR: Node.js is not installed!
    pause
    exit /b 1
)
echo ✅ Node.js is available

REM Check npm
npm --version >nul 2>&1
if errorlevel 1 (
    echo ❌ ERROR: npm is not available!
    pause
    exit /b 1
)
echo ✅ npm is available

REM Step 2: Build Frontend
echo.
echo 🔨 Step 2: Building frontend...

REM Check if frontend directory exists
if not exist "..\billing-frontEnd\my-billing-app" (
    echo ❌ ERROR: Frontend directory not found!
    echo Expected: ..\billing-frontEnd\my-billing-app
    pause
    exit /b 1
)

REM Navigate to frontend directory
cd "..\billing-frontEnd\my-billing-app"

REM Install dependencies if needed
if not exist "node_modules" (
    echo 📦 Installing frontend dependencies...
    npm install
    if errorlevel 1 (
        echo ❌ ERROR: Failed to install frontend dependencies!
        pause
        exit /b 1
    )
)

REM Build frontend
echo 🏗️ Building frontend application...
npm run build
if errorlevel 1 (
    echo ❌ ERROR: Frontend build failed!
    pause
    exit /b 1
)
echo ✅ Frontend built successfully

REM Step 3: Copy frontend files
echo.
echo 📋 Step 3: Copying frontend files to Spring Boot...

REM Navigate back to billing directory
cd "..\..\billing"

REM Create static directory if needed
if not exist "src\main\resources\static" (
    mkdir "src\main\resources\static"
)

REM Clear existing files
if exist "src\main\resources\static\*" (
    del /Q "src\main\resources\static\*" 2>nul
    for /d %%i in ("src\main\resources\static\*") do rmdir /s /q "%%i" 2>nul
)

REM Copy build files
xcopy "..\billing-frontEnd\my-billing-app\dist\*" "src\main\resources\static\" /E /Y /Q
if errorlevel 1 (
    echo ❌ ERROR: Failed to copy frontend files!
    pause
    exit /b 1
)

REM Verify copy
if not exist "src\main\resources\static\index.html" (
    echo ❌ ERROR: Frontend files not copied correctly!
    pause
    exit /b 1
)
echo ✅ Frontend files copied successfully

REM Step 4: Build Docker image
echo.
echo 🐳 Step 4: Building Docker image...
docker build -t %FULL_IMAGE_NAME% .
if errorlevel 1 (
    echo ❌ ERROR: Docker build failed!
    pause
    exit /b 1
)
echo ✅ Docker image built successfully

REM Tag as latest if not already
if not "%IMAGE_TAG%"=="latest" (
    docker tag %FULL_IMAGE_NAME% %DOCKER_USERNAME%/%IMAGE_NAME%:latest
    if errorlevel 1 (
        echo ❌ ERROR: Failed to tag as latest!
        pause
        exit /b 1
    )
    echo ✅ Image tagged as latest
)

REM Step 5: Push to Docker Hub
echo.
echo 🚀 Step 5: Pushing to Docker Hub...

REM Login to Docker Hub
echo 🔐 Logging in to Docker Hub...
echo Please enter your Docker Hub credentials when prompted.
docker login
if errorlevel 1 (
    echo ❌ ERROR: Docker Hub login failed!
    pause
    exit /b 1
)

REM Push main tag
echo 📤 Pushing %FULL_IMAGE_NAME%...
docker push %FULL_IMAGE_NAME%
if errorlevel 1 (
    echo ❌ ERROR: Failed to push main tag!
    pause
    exit /b 1
)
echo ✅ Main tag pushed successfully

REM Push latest tag if different
if not "%IMAGE_TAG%"=="latest" (
    echo 📤 Pushing latest tag...
    docker push %DOCKER_USERNAME%/%IMAGE_NAME%:latest
    if errorlevel 1 (
        echo ❌ ERROR: Failed to push latest tag!
        pause
        exit /b 1
    )
    echo ✅ Latest tag pushed successfully
)

REM Step 6: Summary
echo.
echo ==========================================
echo 🎉 BUILD AND DEPLOYMENT COMPLETE!
echo ==========================================
echo.
echo 📊 Summary:
echo ✅ Frontend built and copied
echo ✅ Docker image created: %FULL_IMAGE_NAME%
echo ✅ Image pushed to Docker Hub
echo.
echo 🌐 Docker Hub Repository:
echo https://hub.docker.com/r/%DOCKER_USERNAME%/%IMAGE_NAME%
echo.
echo 🚀 To run your application:
echo docker run -p 8080:8080 %FULL_IMAGE_NAME%
echo.
echo 📋 Or with docker-compose:
echo docker-compose up -d
echo.
echo 🔗 Application will be available at:
echo http://localhost:8080
echo ==========================================

REM Show final image info
echo.
echo 📊 Final Image Information:
docker images %DOCKER_USERNAME%/%IMAGE_NAME%

echo.
echo Press any key to exit...
pause >nul
