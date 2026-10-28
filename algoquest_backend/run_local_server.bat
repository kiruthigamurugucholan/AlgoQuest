@echo off
echo ========================================================
echo   AlgoQuest — Standalone PHP Development Server
echo ========================================================
echo.

set PHP_BIN=C:\xampp\php\php.exe

if not exist "%PHP_BIN%" (
    echo [ERROR] PHP executable not found at %PHP_BIN%.
    echo Please make sure XAMPP is installed at C:\xampp
    pause
    exit /b 1
)

echo Starting PHP built-in server on http://localhost:80/algoquest_backend/
echo Android Emulator mapping: http://10.0.2.2/algoquest_backend/
echo.
echo Press Ctrl+C to stop the server at any time.
echo.

cd /d "%~dp0\.."
"%PHP_BIN%" -S 0.0.0.0:80
pause

