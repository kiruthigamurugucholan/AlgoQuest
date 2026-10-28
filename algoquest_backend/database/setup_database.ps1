# ========================================================
#   AlgoQuest — Database Setup PowerShell Script
# ========================================================

$mysqlBin = "C:\xampp\mysql\bin\mysql.exe"
if (-not (Test-Path $mysqlBin)) {
    Write-Error "MySQL not found at $mysqlBin. Please check your XAMPP installation."
    exit 1
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$schemaFile = Join-Path $scriptDir "schema.sql"
$seedFile = Join-Path $scriptDir "seed.sql"

Write-Host "Checking MySQL server connection..." -ForegroundColor Cyan
& $mysqlBin -u root -e "SELECT 1;" 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Error "Could not connect to MySQL server. Please start MySQL from the XAMPP Control Panel."
    exit 1
}
Write-Host "MySQL is running!" -ForegroundColor Green

Write-Host "Importing schema.sql..." -ForegroundColor Cyan
Get-Content $schemaFile | & $mysqlBin -u root
if ($LASTEXITCODE -ne 0) {
    Write-Error "Failed to import schema.sql."
    exit 1
}
Write-Host "Schema successfully imported!" -ForegroundColor Green

Write-Host "Importing seed.sql..." -ForegroundColor Cyan
Get-Content $seedFile | & $mysqlBin -u root
if ($LASTEXITCODE -ne 0) {
    Write-Error "Failed to import seed.sql."
    exit 1
}
Write-Host "Seed data successfully imported!" -ForegroundColor Green
Write-Host "AlgoQuest database (algoquest_db) is ready!" -ForegroundColor Green

