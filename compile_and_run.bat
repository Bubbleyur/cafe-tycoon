@echo off
cd /d "d:\1TIK\PROJECT - [1]\PBO\proto1"
javac -d bin src\engine\AssetManager.java src\station\CashierCounter.java src\engine\GamePanel.java
if %errorlevel% equ 0 (
    echo Compilation successful!
    java -cp bin main.Main
) else (
    echo Compilation failed!
)
pause
