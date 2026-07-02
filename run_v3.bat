@echo off
if not exist "out_v3" (
    call compile_v3.bat
)
echo Starting Travel Library System...
java -cp out_v3 Main
