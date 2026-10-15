@echo off
setlocal
cd /d "%~dp0"
chcp 65001 > nul 2>&1

REM SportsPulse - Sports Performance Analytics Platform Launcher
REM Cross-platform location-independent launcher with automatic JAR resolution.

if "%1"=="--edition2" (
    if not exist "bin" mkdir bin
    java -Dfile.encoding=UTF-8 -cp bin org.sportspulse.platform.core.SportsPulseApp %*
    goto :eof
)

if exist "dist\SportsPulse.jar" (
    java -Dfile.encoding=UTF-8 -jar dist\SportsPulse.jar %*
    goto :eof
)

if exist "SportsPulse.jar" (
    java -Dfile.encoding=UTF-8 -jar SportsPulse.jar %*
    goto :eof
)

echo [SportsPulse] Building executable distribution JAR...
call build.bat jar
if exist "dist\SportsPulse.jar" (
    java -Dfile.encoding=UTF-8 -jar dist\SportsPulse.jar %*
) else (
    echo [ERROR] Failed to launch. Please verify that JDK 17+ is installed.
)
