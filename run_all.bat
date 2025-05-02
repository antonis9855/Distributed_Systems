@echo off
setlocal

REM -- 1) Compile everything --
echo Compiling Java sources...
javac org\json\*.java Master.java Reducer.java Worker.java AppManager.java AppClient.java
if errorlevel 1 (
  echo Compilation failed.
  pause
  exit /b 1
)

REM -- 2) Start three Workers --
echo Starting Workers...
start "Worker6000"    cmd /k "java Worker 6000"
start "Worker6001"    cmd /k "java Worker 6001"
start "Worker6002"    cmd /k "java Worker 6002"

REM -- 3) Start Reducer --
echo Starting Reducer...
start "Reducer"       cmd /k "java Reducer 7000"

REM -- 4) Start Master --
echo Starting Master...
start "Master"        cmd /k "java Master"

REM -- 5) Start Manager UI --
echo Starting Manager console...
start "AppManager"    cmd /k "java AppManager"

REM -- 6) Start Client UI --
echo Starting Client console...
start "AppClient"     cmd /k "java AppClient"

echo All processes launched.
endlocal
