@echo off
REM Frontend Build and Copy Script for Billing Application
REM This script builds the frontend and copies files to Spring Boot static resources

echo ========================================
echo Building Frontend and Copying Files
echo ========================================

REM Check if frontend directory exists
if not exist "..\billing-frontEnd\my-billing-app" (
    echo ❌ ERROR: Frontend directory not found!
    echo Expected: ..\billing-frontEnd\my-billing-app
    echo Please ensure the frontend project is in the correct location.
    pause
    exit /b 1
)

REM Navigate to frontend directory
echo 📁 Navigating to frontend directory...
cd "..\billing-frontEnd\my-billing-app"

REM Check if package.json exists
if not exist "package.json" (
    echo ❌ ERROR: package.json not found!
    echo This doesn't appear to be a valid Node.js project.
    pause
    exit /b 1
)

REM Install dependencies if node_modules doesn't exist
if not exist "node_modules" (
    echo 📦 Installing dependencies...
    npm install
    if errorlevel 1 (
        echo ❌ ERROR: Failed to install dependencies!
        pause
        exit /b 1
    )
    echo ✅ Dependencies installed successfully
)

REM Build the frontend
echo 🔨 Building frontend...
npm run build
if errorlevel 1 (
    echo ❌ ERROR: Frontend build failed!
    pause
    exit /b 1
)
echo ✅ Frontend built successfully

REM Check if dist directory was created
if not exist "dist" (
    echo ❌ ERROR: Build output directory 'dist' not found!
    echo The build may have failed or uses a different output directory.
    pause
    exit /b 1
)

REM Navigate back to billing directory
echo 📁 Navigating back to billing directory...
cd "..\..\billing"

REM Create static directory if it doesn't exist
if not exist "src\main\resources\static" (
    echo 📁 Creating static resources directory...
    mkdir "src\main\resources\static"
)

REM Clear existing static files (optional - comment out if you want to keep existing files)
echo 🧹 Clearing existing static files...
if exist "src\main\resources\static\*" (
    del /Q "src\main\resources\static\*"
    for /d %%i in ("src\main\resources\static\*") do rmdir /s /q "%%i"
)

REM Copy build files to static resources
echo 📋 Copying build files to static resources...
xcopy "..\billing-frontEnd\my-billing-app\dist\*" "src\main\resources\static\" /E /Y
if errorlevel 1 (
    echo ❌ ERROR: Failed to copy build files!
    pause
    exit /b 1
)

REM Verify files were copied
if not exist "src\main\resources\static\index.html" (
    echo ❌ ERROR: index.html not found in static resources!
    echo The copy operation may have failed.
    pause
    exit /b 1
)

echo ✅ Build files copied successfully

REM Show what was copied
echo.
echo 📊 Files in static resources:
dir "src\main\resources\static" /B

echo.
echo ========================================
echo 🎉 Frontend build and copy completed!
echo ========================================
echo.
echo Next steps:
echo 1. Run: build-and-push.bat your-dockerhub-username latest
echo 2. Or manually build Docker image: docker build -t your-image-name .
echo ========================================

pause
