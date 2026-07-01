@echo off
if not exist out mkdir out
echo Compiling Java code...
javac -d out -sourcepath src src\Main.java
if %ERRORLEVEL% equ 0 (
    echo Compilation successful! Run run.bat to start.
) else (
    echo Compilation failed.
)
pause
