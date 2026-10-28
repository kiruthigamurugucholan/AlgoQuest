@echo off
echo ========================================================
echo   AlgoQuest — Database Setup & Seed Tool
echo ========================================================
echo.

set MYSQL_BIN=C:\xampp\mysql\bin\mysql.exe

if not exist "%MYSQL_BIN%" (
    echo [ERROR] MySQL executable not found at %MYSQL_BIN%.
    echo Please make sure XAMPP is installed at C:\xampp
    pause
    exit /b 1
)

echo [1/3] Checking MySQL connection...
"%MYSQL_BIN%" -u root -e "SELECT 1;" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Could not connect to MySQL server.
    echo Please ensure MySQL is started in XAMPP Control Panel.
    echo.
    pause
    exit /b 1
)
echo [OK] MySQL is running!
echo.

echo [2/3] Importing Schema (schema.sql)...
"%MYSQL_BIN%" -u root < "%~dp0schema.sql"
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to import schema.sql.
    pause
    exit /b 1
)
echo [OK] Schema imported successfully!
echo.

echo [3/3] Importing Seed Data (seed.sql)...
"%MYSQL_BIN%" -u root < "%~dp0seed.sql"
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to import seed.sql.
    pause
    exit /b 1
)
echo [OK] Seed data imported successfully!
echo.

echo ========================================================
echo   AlgoQuest Database (algoquest_db) setup is COMPLETE!
echo ========================================================
echo.
pause

