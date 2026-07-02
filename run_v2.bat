@echo off
if not exist "out_v2" (
    call compile_v2.bat
)
echo Starting Travel Library System...
java -cp out_v2 Main
