@echo off
cd /d "d:\1TIK\PROJECT - [1]\PBO\cafe-tycoon"
echo Compiling GamePanel.java...
javac -d bin src/engine/GamePanel.java 2>&1
if %errorlevel% equ 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
)
