# Build and Push Script for Billing Application Docker Image
# Usage: .\build-and-push.ps1 [DockerHubUsername] [TagVersion]

param(
    [string]$DockerUsername = "",
    [string]$ImageTag = "latest"
)

# Configuration
$DefaultUsername = "your-dockerhub-username"
$ImageName = "billing-app"

# Get Docker Hub username
if ([string]::IsNullOrEmpty($DockerUsername)) {
    $DockerUsername = Read-Host "Enter your Docker Hub username [$DefaultUsername]"
    if ([string]::IsNullOrEmpty($DockerUsername)) {
        $DockerUsername = $DefaultUsername
    }
}

$FullImageName = "$DockerUsername/$ImageName`:$ImageTag"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Building and Pushing Billing Application" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Docker Hub Username: $DockerUsername" -ForegroundColor Yellow
Write-Host "Image Name: $ImageName" -ForegroundColor Yellow
Write-Host "Tag: $ImageTag" -ForegroundColor Yellow
Write-Host "Full Image: $FullImageName" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

# Check if Docker is running
Write-Host "Checking Docker status..." -ForegroundColor Blue
try {
    $dockerVersion = docker version 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Docker command failed"
    }
    Write-Host "✅ Docker is running" -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR: Docker is not running or not installed!" -ForegroundColor Red
    Write-Host "Please start Docker Desktop and try again." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if frontend build files exist
Write-Host "Checking frontend build files..." -ForegroundColor Blue
if (-not (Test-Path "src\main\resources\static\index.html")) {
    Write-Host "❌ ERROR: Frontend build files not found!" -ForegroundColor Red
    Write-Host "Please build the frontend and copy the build files to src\main\resources\static\" -ForegroundColor Red
    Write-Host ""
    Write-Host "Steps to build frontend:" -ForegroundColor Yellow
    Write-Host "1. Navigate to billing-frontEnd\my-billing-app" -ForegroundColor Yellow
    Write-Host "2. Run: npm run build" -ForegroundColor Yellow
    Write-Host "3. Copy contents of dist\ folder to src\main\resources\static\" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "✅ Frontend build files found" -ForegroundColor Green

# Build the Docker image
Write-Host ""
Write-Host "🔨 Building Docker image..." -ForegroundColor Blue
Write-Host "Command: docker build -t $FullImageName ." -ForegroundColor Gray
$buildResult = docker build -t $FullImageName .
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERROR: Docker build failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "✅ Docker image built successfully" -ForegroundColor Green

# Tag the image as latest if not already latest
if ($ImageTag -ne "latest") {
    Write-Host ""
    Write-Host "🏷️ Tagging image as latest..." -ForegroundColor Blue
    docker tag $FullImageName "$DockerUsername/$ImageName`:latest"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ ERROR: Failed to tag image as latest!" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Host "✅ Image tagged as latest" -ForegroundColor Green
}

# Login to Docker Hub
Write-Host ""
Write-Host "🔐 Logging in to Docker Hub..." -ForegroundColor Blue
Write-Host "Please enter your Docker Hub credentials when prompted." -ForegroundColor Yellow
docker login
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERROR: Docker Hub login failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "✅ Successfully logged in to Docker Hub" -ForegroundColor Green

# Push the image
Write-Host ""
Write-Host "🚀 Pushing image to Docker Hub..." -ForegroundColor Blue
Write-Host "Command: docker push $FullImageName" -ForegroundColor Gray
docker push $FullImageName
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERROR: Failed to push image to Docker Hub!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "✅ Image pushed successfully" -ForegroundColor Green

# Push latest tag if different from main tag
if ($ImageTag -ne "latest") {
    Write-Host ""
    Write-Host "🚀 Pushing latest tag..." -ForegroundColor Blue
    docker push "$DockerUsername/$ImageName`:latest"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ ERROR: Failed to push latest tag!" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Host "✅ Latest tag pushed successfully" -ForegroundColor Green
}

# Show image information
Write-Host ""
Write-Host "📊 Image Information:" -ForegroundColor Blue
docker images "$DockerUsername/$ImageName"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🎉 SUCCESS! Image pushed to Docker Hub" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Image: $FullImageName" -ForegroundColor Yellow
Write-Host "Docker Hub URL: https://hub.docker.com/r/$DockerUsername/$ImageName" -ForegroundColor Yellow
Write-Host ""
Write-Host "To run the image:" -ForegroundColor Blue
Write-Host "docker run -p 8080:8080 $FullImageName" -ForegroundColor Gray
Write-Host ""
Write-Host "To run with docker-compose:" -ForegroundColor Blue
Write-Host "docker-compose up -d" -ForegroundColor Gray
Write-Host "========================================" -ForegroundColor Cyan

Read-Host "Press Enter to exit"
