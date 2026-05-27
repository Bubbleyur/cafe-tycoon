@echo off
setlocal enabledelayedexpansion
cd /d "d:\1TIK\PROJECT - [1]\PBO\proto1"

REM Compile all Java files
echo Compiling Java files...
javac -d bin src\*.java src\engine\*.java src\entity\*.java src\station\*.java src\logic\*.java src\ui\*.java src\main\*.java 2>&1

if %errorlevel% equ 0 (
    echo.
    echo Compilation successful!
    echo.
    pause
) else (
    echo.
    echo Compilation failed - check errors above
    echo.
    pause
)
