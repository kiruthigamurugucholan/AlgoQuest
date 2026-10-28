@echo off
echo ========================================================
echo   AlgoQuest — Deploy Backend to XAMPP htdocs
echo ========================================================
echo.

set DEST=C:\xampp\htdocs\algoquest_backend
set SRC=%~dp0

if not exist "C:\xampp\htdocs" (
    echo [ERROR] Directory C:\xampp\htdocs not found!
    echo Please make sure XAMPP is installed at C:\xampp
    pause
    exit /b 1
)

echo Syncing backend files to %DEST%...
robocopy "%SRC%" "%DEST%" /E /XD .git tests /XF deploy_to_xampp.*

echo.
echo ========================================================
echo   Backend deployed to: %DEST%
echo   API URL: http://localhost/algoquest_backend/api/index.php
echo   Emulator URL: http://10.0.2.2/algoquest_backend/api/index.php
echo ========================================================
echo.
pause

