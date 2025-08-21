# Docker Desktop Status Checker
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Docker Desktop Status Check" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Check if Docker Desktop process is running
Write-Host "1. Checking Docker Desktop process..." -ForegroundColor Blue
$dockerProcess = Get-Process "Docker Desktop" -ErrorAction SilentlyContinue
if ($dockerProcess) {
    Write-Host "✅ Docker Desktop process is running" -ForegroundColor Green
    Write-Host "   Process ID: $($dockerProcess.Id)" -ForegroundColor Gray
} else {
    Write-Host "❌ Docker Desktop process is NOT running" -ForegroundColor Red
    Write-Host "   Please start Docker Desktop manually" -ForegroundColor Yellow
}

# Check Docker services
Write-Host "`n2. Checking Docker services..." -ForegroundColor Blue
$dockerServices = Get-Service "*docker*" -ErrorAction SilentlyContinue
if ($dockerServices) {
    foreach ($service in $dockerServices) {
        $status = if ($service.Status -eq "Running") { "✅" } else { "❌" }
        $color = if ($service.Status -eq "Running") { "Green" } else { "Red" }
        Write-Host "   $status $($service.Name): $($service.Status)" -ForegroundColor $color
    }
} else {
    Write-Host "❌ No Docker services found" -ForegroundColor Red
}

# Check Docker daemon connection
Write-Host "`n3. Testing Docker daemon connection..." -ForegroundColor Blue
try {
    $dockerVersion = docker version --format json 2>$null | ConvertFrom-Json
    if ($dockerVersion) {
        Write-Host "✅ Docker daemon is accessible" -ForegroundColor Green
        Write-Host "   Client Version: $($dockerVersion.Client.Version)" -ForegroundColor Gray
        Write-Host "   Server Version: $($dockerVersion.Server.Version)" -ForegroundColor Gray
    }
} catch {
    Write-Host "❌ Cannot connect to Docker daemon" -ForegroundColor Red
    Write-Host "   Error: Docker daemon is not running" -ForegroundColor Yellow
}

# Check Docker Desktop installation path
Write-Host "`n4. Checking Docker Desktop installation..." -ForegroundColor Blue
$dockerPaths = @(
    "C:\Program Files\Docker\Docker\Docker Desktop.exe",
    "C:\Users\$env:USERNAME\AppData\Local\Docker\Docker Desktop.exe"
)

$dockerFound = $false
foreach ($path in $dockerPaths) {
    if (Test-Path $path) {
        Write-Host "✅ Docker Desktop found at: $path" -ForegroundColor Green
        $dockerFound = $true
        break
    }
}

if (-not $dockerFound) {
    Write-Host "❌ Docker Desktop executable not found" -ForegroundColor Red
    Write-Host "   Please reinstall Docker Desktop" -ForegroundColor Yellow
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Recommendations:" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

if (-not $dockerProcess) {
    Write-Host "1. Start Docker Desktop:" -ForegroundColor Yellow
    Write-Host "   - Search for 'Docker Desktop' in Start Menu" -ForegroundColor Gray
    Write-Host "   - Or run: Start-Process 'Docker Desktop'" -ForegroundColor Gray
    Write-Host "   - Wait for 'Engine running' status" -ForegroundColor Gray
}

Write-Host "2. If Docker Desktop won't start:" -ForegroundColor Yellow
Write-Host "   - Restart as Administrator" -ForegroundColor Gray
Write-Host "   - Check Windows features (Hyper-V, WSL2)" -ForegroundColor Gray
Write-Host "   - Restart your computer" -ForegroundColor Gray

Write-Host "3. Alternative: Reset Docker Desktop:" -ForegroundColor Yellow
Write-Host "   - Right-click Docker Desktop in system tray" -ForegroundColor Gray
Write-Host "   - Select 'Troubleshoot' > 'Reset to factory defaults'" -ForegroundColor Gray

Write-Host "`nPress any key to continue..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
