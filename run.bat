@echo off
setlocal enabledelayedexpansion

echo Starting Spring Boot Application...
echo.

REM Check if gradlew.bat exists
if not exist "%~dp0gradlew.bat" (
    echo Error: gradlew.bat not found!
    exit /b 1
)

REM Run with dev profile
call "%~dp0gradlew.bat" bootRun --args="--spring.profiles.active=dev"

pause
