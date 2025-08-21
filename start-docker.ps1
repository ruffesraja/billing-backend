# Docker Desktop Starter Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting Docker Desktop" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Function to check if running as administrator
function Test-Administrator {
    $currentUser = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($currentUser)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

# Check if running as administrator
if (-not (Test-Administrator)) {
    Write-Host "⚠️  Not running as Administrator" -ForegroundColor Yellow
    Write-Host "   For best results, run PowerShell as Administrator" -ForegroundColor Gray
}

# Stop existing Docker Desktop processes
Write-Host "1. Stopping existing Docker Desktop processes..." -ForegroundColor Blue
Get-Process "Docker Desktop" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3

# Find Docker Desktop executable
$dockerPaths = @(
    "C:\Program Files\Docker\Docker\Docker Desktop.exe",
    "C:\Users\$env:USERNAME\AppData\Local\Docker\Docker Desktop.exe"
)

$dockerExe = $null
foreach ($path in $dockerPaths) {
    if (Test-Path $path) {
        $dockerExe = $path
        break
    }
}

if (-not $dockerExe) {
    Write-Host "❌ Docker Desktop executable not found!" -ForegroundColor Red
    Write-Host "   Please install Docker Desktop from https://docker.com" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "✅ Found Docker Desktop at: $dockerExe" -ForegroundColor Green

# Start Docker Desktop
Write-Host "`n2. Starting Docker Desktop..." -ForegroundColor Blue
try {
    Start-Process -FilePath $dockerExe -WindowStyle Hidden
    Write-Host "✅ Docker Desktop started" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to start Docker Desktop: $($_.Exception.Message)" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Wait for Docker Desktop to initialize
Write-Host "`n3. Waiting for Docker Desktop to initialize..." -ForegroundColor Blue
Write-Host "   This may take 2-3 minutes..." -ForegroundColor Gray

$maxWaitTime = 180 # 3 minutes
$waitInterval = 10
$elapsedTime = 0

while ($elapsedTime -lt $maxWaitTime) {
    Start-Sleep -Seconds $waitInterval
    $elapsedTime += $waitInterval
    
    # Check if Docker Desktop process is running
    $dockerProcess = Get-Process "Docker Desktop" -ErrorAction SilentlyContinue
    if (-not $dockerProcess) {
        Write-Host "❌ Docker Desktop process stopped unexpectedly" -ForegroundColor Red
        break
    }
    
    # Test Docker daemon connection
    try {
        $null = docker version 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Docker daemon is ready!" -ForegroundColor Green
            break
        }
    } catch {
        # Continue waiting
    }
    
    $remainingTime = $maxWaitTime - $elapsedTime
    Write-Host "   Still waiting... ($elapsedTime/$maxWaitTime seconds, $remainingTime seconds remaining)" -ForegroundColor Gray
}

# Final check
Write-Host "`n4. Final verification..." -ForegroundColor Blue
try {
    $dockerVersion = docker version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ SUCCESS! Docker is now running" -ForegroundColor Green
        Write-Host "`nDocker version information:" -ForegroundColor Blue
        docker version --format "Client: {{.Client.Version}}, Server: {{.Server.Version}}"
        
        Write-Host "`n========================================" -ForegroundColor Cyan
        Write-Host "🎉 Docker Desktop is ready!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "You can now run your Docker commands:" -ForegroundColor Yellow
        Write-Host "   docker build -t ruffes/billing-app:latest ." -ForegroundColor Gray
        Write-Host "   docker images" -ForegroundColor Gray
        Write-Host "   docker run -p 8080:8080 ruffes/billing-app:latest" -ForegroundColor Gray
    } else {
        Write-Host "❌ Docker daemon is still not accessible" -ForegroundColor Red
        Write-Host "`nTroubleshooting steps:" -ForegroundColor Yellow
        Write-Host "1. Check Docker Desktop system tray icon (should be green)" -ForegroundColor Gray
        Write-Host "2. Open Docker Desktop and check for error messages" -ForegroundColor Gray
        Write-Host "3. Try restarting Docker Desktop manually" -ForegroundColor Gray
        Write-Host "4. Restart your computer if issues persist" -ForegroundColor Gray
    }
} catch {
    Write-Host "❌ Error checking Docker status: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nPress any key to continue..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
