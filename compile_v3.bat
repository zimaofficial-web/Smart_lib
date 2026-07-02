@echo off
echo Compiling Dam's Library System (src_v3)...
if not exist "out_v3" mkdir out_v3
javac -d out_v3 src_v3\model\*.java src_v3\utils\*.java src_v3\controller\*.java src_v3\gui\*.java src_v3\Main.java
if %errorlevel% neq 0 (
    echo Compilation Failed!
    pause
    exit /b %errorlevel%
)
echo Compilation Successful!
