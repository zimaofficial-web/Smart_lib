@echo off
echo Compiling Dam's Library System (src_v2)...
if not exist "out_v2" mkdir out_v2
javac -d out_v2 src_v2\model\*.java src_v2\utils\*.java src_v2\controller\*.java src_v2\gui\*.java src_v2\Main.java
if %errorlevel% neq 0 (
    echo Compilation Failed!
    pause
    exit /b %errorlevel%
)
echo Compilation Successful!
