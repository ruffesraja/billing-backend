# Docker Deployment Guide for Billing Application

This guide provides complete instructions for building and deploying the Billing Application using Docker from your Windows environment.

## 📋 Prerequisites

### Required Software
- **Docker Desktop for Windows** (latest version)
- **Node.js and npm** (for building frontend)
- **Git** (for version control)
- **Docker Hub Account** (for pushing images)

### Verify Prerequisites
```bash
# Check Docker
docker --version
docker-compose --version

# Check Node.js
node --version
npm --version
```

## 🏗️ Build Process Overview

### Step 1: Build Frontend
```bash
# Navigate to frontend directory
cd "billing-frontEnd/my-billing-app"

# Install dependencies (if not already done)
npm install

# Build the frontend
npm run build
```

### Step 2: Copy Frontend Build Files
```bash
# Copy all files from dist/ to src/main/resources/static/
# Windows Command Prompt:
xcopy "billing-frontEnd\my-billing-app\dist\*" "billing\src\main\resources\static\" /E /Y

# Or PowerShell:
Copy-Item -Path "billing-frontEnd\my-billing-app\dist\*" -Destination "billing\src\main\resources\static\" -Recurse -Force
```

### Step 3: Build and Push Docker Image
```bash
# Navigate to billing directory
cd billing

# Option 1: Use batch script (Command Prompt)
build-and-push.bat your-dockerhub-username v1.0.0

# Option 2: Use PowerShell script
.\build-and-push.ps1 -DockerUsername "your-dockerhub-username" -ImageTag "v1.0.0"

# Option 3: Manual commands (see Manual Build section)
```

## 🚀 Quick Start (Automated)

### Using Batch Script (Windows CMD)
```cmd
cd "F:\rahul pro v1\billing"
build-and-push.bat your-dockerhub-username latest
```

### Using PowerShell Script
```powershell
cd "F:\rahul pro v1\billing"
.\build-and-push.ps1 -DockerUsername "your-dockerhub-username" -ImageTag "latest"
```

## 🔧 Manual Build Process

If you prefer to run commands manually:

### 1. Prepare Frontend
```bash
# Build frontend
cd "F:\rahul pro v1\billing-frontEnd\my-billing-app"
npm run build

# Copy to Spring Boot static resources
cd "F:\rahul pro v1\billing"
xcopy "..\billing-frontEnd\my-billing-app\dist\*" "src\main\resources\static\" /E /Y
```

### 2. Build Docker Image
```bash
# Build the image
docker build -t your-dockerhub-username/billing-app:latest .

# Tag with version (optional)
docker tag your-dockerhub-username/billing-app:latest your-dockerhub-username/billing-app:v1.0.0
```

### 3. Push to Docker Hub
```bash
# Login to Docker Hub
docker login

# Push the images
docker push your-dockerhub-username/billing-app:latest
docker push your-dockerhub-username/billing-app:v1.0.0
```

## 🐳 Docker Configuration

### Dockerfile Features
- **Multi-stage build**: Optimized for size and security
- **Non-root user**: Runs as `billing` user for security
- **Health checks**: Built-in health monitoring
- **JVM optimization**: Configured for containerized environment
- **Layer caching**: Efficient builds with dependency caching

### Environment Variables
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/billing_db
SPRING_DATASOURCE_USERNAME=billing_user
SPRING_DATASOURCE_PASSWORD=billing_password

# Application Configuration
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8080

# JVM Configuration
JAVA_OPTS=-Xmx512m -Xms256m
```

## 🏃‍♂️ Running the Application

### Option 1: Docker Run (Simple)
```bash
# Run the application
docker run -p 8080:8080 your-dockerhub-username/billing-app:latest

# Run with environment variables
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e JAVA_OPTS="-Xmx1g -Xms512m" \
  your-dockerhub-username/billing-app:latest
```

### Option 2: Docker Compose (Recommended)
```bash
# Start the full stack (app + database)
docker-compose up -d

# View logs
docker-compose logs -f billing-app

# Stop the stack
docker-compose down
```

### Option 3: Production Deployment
```bash
# Pull and run the latest image
docker pull your-dockerhub-username/billing-app:latest
docker run -d \
  --name billing-app \
  -p 8080:8080 \
  --restart unless-stopped \
  -e SPRING_PROFILES_ACTIVE=production \
  your-dockerhub-username/billing-app:latest
```

## 📁 Project Structure

```
billing/
├── Dockerfile                 # Multi-stage Docker build
├── docker-compose.yml         # Full stack deployment
├── .dockerignore              # Docker build optimization
├── init-db.sql               # Database initialization
├── build-and-push.bat        # Windows batch script
├── build-and-push.ps1        # PowerShell script
├── src/
│   └── main/
│       └── resources/
│           └── static/        # Frontend build files (copy here)
│               ├── index.html
│               ├── assets/
│               └── ...
└── pom.xml                   # Maven configuration
```

## 🔍 Troubleshooting

### Common Issues

#### 1. Frontend Build Files Missing
```
❌ ERROR: Frontend build files not found!
```
**Solution**: Build frontend and copy files to `src/main/resources/static/`

#### 2. Docker Not Running
```
❌ ERROR: Docker is not running or not installed!
```
**Solution**: Start Docker Desktop for Windows

#### 3. Docker Hub Login Failed
```
❌ ERROR: Docker Hub login failed!
```
**Solution**: Check credentials and network connection

#### 4. Build Failed
```
❌ ERROR: Docker build failed!
```
**Solution**: Check Dockerfile and build context

### Verification Commands
```bash
# Check if frontend files are copied
dir "src\main\resources\static"

# Check Docker images
docker images your-dockerhub-username/billing-app

# Test the application
curl http://localhost:8080/api/invoices

# Check container logs
docker logs billing-app
```

## 🌐 Accessing the Application

### Local Development
- **Application**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html (if enabled)
- **Health Check**: http://localhost:8080/actuator/health

### Docker Hub Repository
- **Repository**: https://hub.docker.com/r/your-dockerhub-username/billing-app
- **Tags**: View all available versions

## 🔒 Security Considerations

### Image Security
- **Non-root user**: Application runs as `billing` user
- **Minimal base image**: Uses `openjdk:17-jdk-slim`
- **No sensitive data**: Credentials passed via environment variables
- **Health checks**: Built-in monitoring

### Production Recommendations
```bash
# Use specific tags (not latest) in production
docker run -p 8080:8080 your-dockerhub-username/billing-app:v1.0.0

# Set resource limits
docker run -p 8080:8080 \
  --memory=1g \
  --cpus=1.0 \
  your-dockerhub-username/billing-app:v1.0.0

# Use secrets for sensitive data
docker run -p 8080:8080 \
  --env-file .env \
  your-dockerhub-username/billing-app:v1.0.0
```

## 📊 Monitoring and Logs

### Container Monitoring
```bash
# View container stats
docker stats billing-app

# View logs
docker logs -f billing-app

# Execute commands in container
docker exec -it billing-app bash
```

### Health Checks
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Docker health status
docker inspect --format='{{.State.Health.Status}}' billing-app
```

## 🚀 CI/CD Integration

### GitHub Actions Example
```yaml
name: Build and Push Docker Image

on:
  push:
    branches: [ main ]
    tags: [ 'v*' ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Build Frontend
      run: |
        cd billing-frontEnd/my-billing-app
        npm install
        npm run build
        cp -r dist/* ../../billing/src/main/resources/static/
    
    - name: Build and Push Docker Image
      run: |
        cd billing
        docker build -t ${{ secrets.DOCKER_USERNAME }}/billing-app:latest .
        echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
        docker push ${{ secrets.DOCKER_USERNAME }}/billing-app:latest
```

## 📝 Version Management

### Tagging Strategy
```bash
# Development builds
your-dockerhub-username/billing-app:dev

# Release candidates
your-dockerhub-username/billing-app:v1.0.0-rc1

# Production releases
your-dockerhub-username/billing-app:v1.0.0
your-dockerhub-username/billing-app:latest
```

### Build Script Usage
```bash
# Development build
build-and-push.bat your-dockerhub-username dev

# Release candidate
build-and-push.bat your-dockerhub-username v1.0.0-rc1

# Production release
build-and-push.bat your-dockerhub-username v1.0.0
```

## 🎯 Next Steps

1. **Build Frontend**: `npm run build` in frontend directory
2. **Copy Files**: Copy `dist/*` to `src/main/resources/static/`
3. **Run Script**: Execute `build-and-push.bat your-dockerhub-username latest`
4. **Verify**: Check Docker Hub for your published image
5. **Deploy**: Use `docker run` or `docker-compose` to deploy

## 📞 Support

If you encounter issues:
1. Check the troubleshooting section above
2. Verify all prerequisites are installed
3. Ensure Docker Desktop is running
4. Check Docker Hub credentials
5. Review build logs for specific errors

---

**Happy Dockerizing! 🐳**
