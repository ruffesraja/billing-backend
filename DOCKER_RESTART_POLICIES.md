# Docker Restart Policies Guide

## 🔄 Restart Policy Options

### **1. `no` (Default)**
```yaml
restart: "no"
```
- ❌ Container will NOT restart automatically
- ❌ Will NOT restart after system reboot
- Use case: One-time tasks, development testing

### **2. `always`**
```yaml
restart: always
```
- ✅ Container restarts automatically on failure
- ✅ Container restarts after system reboot
- ✅ Container restarts even if manually stopped (unless Docker daemon is stopped)
- Use case: Critical services that must always run

### **3. `unless-stopped` (Recommended for Production)**
```yaml
restart: unless-stopped
```
- ✅ Container restarts automatically on failure
- ✅ Container restarts after system reboot
- ❌ Container will NOT restart if manually stopped with `docker stop`
- Use case: Production services (current configuration)

### **4. `on-failure[:max-retries]`**
```yaml
restart: on-failure:3
```
- ✅ Container restarts only if it exits with non-zero status
- ✅ Can limit number of restart attempts
- ❌ Will NOT restart after system reboot if container was running normally
- Use case: Services that might fail temporarily

## 🎯 Current Billing App Configuration

Your billing application uses `unless-stopped`:

```yaml
services:
  postgres:
    restart: unless-stopped    # ✅ Auto-restart after reboot
    
  billing-app:
    restart: unless-stopped    # ✅ Auto-restart after reboot
```

## ✅ What This Means

### **After System Reboot:**
1. Docker Desktop starts automatically (if configured)
2. PostgreSQL container starts automatically
3. Billing app container waits for PostgreSQL health check
4. Billing app container starts automatically
5. Your application is available at http://localhost:8081

### **After Container Crash:**
1. Container automatically restarts
2. Health checks ensure proper startup
3. Service becomes available again

### **After Manual Stop:**
1. If you run `docker-compose down` - containers will NOT restart
2. If you run `docker stop billing-app` - container will NOT restart
3. Only restarts on system reboot or container crash

## 🔧 Verification Commands

### **Check Current Restart Policy:**
```bash
# Check restart policy for running containers
docker inspect billing-app-docker | grep -A 5 "RestartPolicy"
docker inspect billing-postgres-docker | grep -A 5 "RestartPolicy"
```

### **Check Container Status:**
```bash
# See restart policy in container list
docker ps -a --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Detailed container information
docker-compose ps
```

## 🚀 Ensuring Auto-Start After Reboot

### **1. Docker Desktop Auto-Start (Windows)**
Ensure Docker Desktop starts with Windows:
- Open Docker Desktop Settings
- General → "Start Docker Desktop when you log in" ✅

### **2. Verify Docker Service (Windows)**
```powershell
# Check if Docker service starts automatically
Get-Service "*docker*" | Select-Object Name, StartType, Status

# Set Docker service to start automatically (if needed)
Set-Service -Name "com.docker.service" -StartupType Automatic
```

### **3. Test Auto-Restart**
```bash
# Test restart behavior
docker-compose up -d

# Simulate system reboot (restart Docker Desktop)
# 1. Quit Docker Desktop
# 2. Start Docker Desktop
# 3. Check if containers auto-started
docker-compose ps
```

## 🔧 Alternative Restart Configurations

### **For Maximum Availability (Always Restart):**
```yaml
services:
  postgres:
    restart: always
    
  billing-app:
    restart: always
```

### **For Development (No Auto-Restart):**
```yaml
services:
  postgres:
    restart: "no"
    
  billing-app:
    restart: "no"
```

### **For Fault-Tolerant Services:**
```yaml
services:
  postgres:
    restart: on-failure:5    # Retry up to 5 times
    
  billing-app:
    restart: on-failure:3    # Retry up to 3 times
```

## 📊 Monitoring Auto-Restart

### **Check Container Uptime:**
```bash
# See how long containers have been running
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.RunningFor}}"
```

### **View Restart Count:**
```bash
# Check restart history
docker inspect billing-app-docker | grep -A 10 "RestartCount"
```

### **Monitor Container Events:**
```bash
# Watch container events in real-time
docker events --filter container=billing-app-docker
```

## 🎯 Production Recommendations

### **For Production Servers:**
```yaml
services:
  postgres:
    restart: unless-stopped
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '1.0'
    
  billing-app:
    restart: unless-stopped
    deploy:
      resources:
        limits:
          memory: 512M
          cpus: '0.5'
```

### **For High Availability:**
```yaml
services:
  postgres:
    restart: always
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U billing_user -d billing_db"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 30s
    
  billing-app:
    restart: always
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s
```

## 🚨 Troubleshooting Auto-Restart Issues

### **Container Not Starting After Reboot:**
1. Check Docker Desktop is running
2. Check restart policy: `docker inspect container-name`
3. Check container logs: `docker-compose logs container-name`
4. Check system resources (memory, disk space)

### **Container Keeps Restarting (Restart Loop):**
1. Check application logs: `docker-compose logs -f billing-app`
2. Check health check configuration
3. Verify database connectivity
4. Check resource limits

### **Docker Desktop Not Starting:**
1. Check Windows startup programs
2. Run Docker Desktop as Administrator
3. Check Windows services: `Get-Service "*docker*"`
4. Reinstall Docker Desktop if necessary

## 📋 Quick Reference Commands

```bash
# Start with auto-restart
docker-compose up -d

# Check restart policies
docker inspect --format='{{.HostConfig.RestartPolicy.Name}}' billing-app-docker

# View container status
docker-compose ps

# Check uptime and restart count
docker stats --no-stream

# Test restart behavior
docker restart billing-app-docker

# Stop without restart
docker-compose down

# Start after manual stop
docker-compose up -d
```

## ✅ Summary

Your current configuration with `restart: unless-stopped` provides:
- ✅ Automatic restart after system reboot
- ✅ Automatic restart after container crash
- ✅ Manual control (won't restart if manually stopped)
- ✅ Production-ready reliability
- ✅ Resource efficiency

This is the recommended configuration for most production deployments!
