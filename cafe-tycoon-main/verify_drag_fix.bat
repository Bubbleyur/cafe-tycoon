@echo off
setlocal enabledelayedexpansion

cd /d "d:\1TIK\PROJECT - [1]\PBO\cafe-tycoon"

echo.
echo ====================================
echo Compiling CustomerLogic.java
echo ====================================
javac -d bin src/logic/CustomerLogic.java 2>&1
if !errorlevel! neq 0 (
    echo ERROR: CustomerLogic compilation failed!
    exit /b 1
)
echo ✓ CustomerLogic compiled successfully

echo.
echo ====================================
echo Compiling GamePanel.java
echo ====================================
javac -d bin src/engine/GamePanel.java 2>&1
if !errorlevel! neq 0 (
    echo ERROR: GamePanel compilation failed!
    exit /b 1
)
echo ✓ GamePanel compiled successfully

echo.
echo ====================================
echo All compilations successful!
echo ====================================
