@echo off
cd /d "%~dp0.."
if not exist bin mkdir bin

echo Compiling...
javac -encoding UTF-8 -d bin src\engine\*.java src\tools\AssetExporter.java
if %errorlevel% neq 0 exit /b 1

echo Exporting cropped sprites from src/assets sheets...
java -cp bin tools.AssetExporter
pause
