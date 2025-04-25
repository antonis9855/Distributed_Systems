@echo off
REM Compile Java sources
javac -d . org\json\*.java *.java
if errorlevel 1 (
  echo Compilation failed.
  pause
  exit /b 1
)

REM Start Workers on ports 6000, 6001, 6002
start "Worker 6000" cmd /k "java Worker 6000"
start "Worker 6001" cmd /k "java Worker 6001"
start "Worker 6002" cmd /k "java Worker 6002"

REM Start Master
start "Master" cmd /k "java Master"

REM Start Manager console
start "Manager" cmd /k "java AppManager"

REM Start Client console
start "Client" cmd /k "java AppClient"
