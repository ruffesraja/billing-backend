@echo off
REM Build and Push Script for Billing Application Docker Image
REM Usage: build-and-push.bat [your-dockerhub-username] [tag-version]

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

echo ========================================
echo Building and Pushing Billing Application
echo ========================================
echo Docker Hub Username: %DOCKER_USERNAME%
echo Image Name: %IMAGE_NAME%
echo Tag: %IMAGE_TAG%
echo Full Image: %FULL_IMAGE_NAME%
echo ========================================

REM Check if Docker is running
echo Checking Docker status...
docker version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker is not running or not installed!
    echo Please start Docker Desktop and try again.
    pause
    exit /b 1
)
echo ✅ Docker is running

REM Check if frontend build files exist
echo Checking frontend build files...
if not exist "src\main\resources\static\index.html" (
    echo ❌ ERROR: Frontend build files not found!
    echo Please build the frontend and copy the build files to src\main\resources\static\
    echo.
    echo Steps to build frontend:
    echo 1. Navigate to billing-frontEnd\my-billing-app
    echo 2. Run: npm run build
    echo 3. Copy contents of dist\ folder to src\main\resources\static\
    echo.
    pause
    exit /b 1
)
echo ✅ Frontend build files found

REM Build the Docker image
echo.
echo 🔨 Building Docker image...
echo Command: docker build -t %FULL_IMAGE_NAME% .
docker build -t %FULL_IMAGE_NAME% .
if errorlevel 1 (
    echo ❌ ERROR: Docker build failed!
    pause
    exit /b 1
)
echo ✅ Docker image built successfully

REM Tag the image as latest if not already latest
if not "%IMAGE_TAG%"=="latest" (
    echo.
    echo 🏷️  Tagging image as latest...
    docker tag %FULL_IMAGE_NAME% %DOCKER_USERNAME%/%IMAGE_NAME%:latest
    if errorlevel 1 (
        echo ❌ ERROR: Failed to tag image as latest!
        pause
        exit /b 1
    )
    echo ✅ Image tagged as latest
)

REM Login to Docker Hub
echo.
echo 🔐 Logging in to Docker Hub...
echo Please enter your Docker Hub credentials when prompted.
docker login
if errorlevel 1 (
    echo ❌ ERROR: Docker Hub login failed!
    pause
    exit /b 1
)
echo ✅ Successfully logged in to Docker Hub

REM Push the image
echo.
echo 🚀 Pushing image to Docker Hub...
echo Command: docker push %FULL_IMAGE_NAME%
docker push %FULL_IMAGE_NAME%
if errorlevel 1 (
    echo ❌ ERROR: Failed to push image to Docker Hub!
    pause
    exit /b 1
)
echo ✅ Image pushed successfully

REM Push latest tag if different from main tag
if not "%IMAGE_TAG%"=="latest" (
    echo.
    echo 🚀 Pushing latest tag...
    docker push %DOCKER_USERNAME%/%IMAGE_NAME%:latest
    if errorlevel 1 (
        echo ❌ ERROR: Failed to push latest tag!
        pause
        exit /b 1
    )
    echo ✅ Latest tag pushed successfully
)

REM Show image information
echo.
echo 📊 Image Information:
docker images %DOCKER_USERNAME%/%IMAGE_NAME%

echo.
echo ========================================
echo 🎉 SUCCESS! Image pushed to Docker Hub
echo ========================================
echo Image: %FULL_IMAGE_NAME%
echo Docker Hub URL: https://hub.docker.com/r/%DOCKER_USERNAME%/%IMAGE_NAME%
echo.
echo To run the image:
echo docker run -p 8080:8080 %FULL_IMAGE_NAME%
echo.
echo To run with docker-compose:
echo docker-compose up -d
echo ========================================

pause
