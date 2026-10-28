# ========================================================
#   AlgoQuest — Deploy Backend to XAMPP htdocs (PowerShell)
# ========================================================

$dest = "C:\xampp\htdocs\algoquest_backend"
$src = Split-Path -Parent $MyInvocation.MyCommand.Path

if (-not (Test-Path "C:\xampp\htdocs")) {
    Write-Error "C:\xampp\htdocs not found. Please verify XAMPP installation."
    exit 1
}

Write-Host "Deploying backend files from $src to $dest..." -ForegroundColor Cyan
Copy-Item -Path "$src\*" -Destination $dest -Recurse -Force
if (Test-Path "$src\.htaccess") { Copy-Item -Path "$src\.htaccess" -Destination "$dest\.htaccess" -Force }
if (Test-Path "$src\api\.htaccess") { Copy-Item -Path "$src\api\.htaccess" -Destination "$dest\api\.htaccess" -Force }
Write-Host "Deployment complete!" -ForegroundColor Green
Write-Host "Backend is available at: http://localhost/algoquest_backend/api/index.php" -ForegroundColor Yellow
Write-Host "Android Emulator base URL: http://10.0.2.2/algoquest_backend/api/index.php?route=" -ForegroundColor Yellow

