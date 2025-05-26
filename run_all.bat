@echo off
setlocal

REM === Path to your JSON library (if using a JAR) ===
set JSON_JAR=json-20210307.jar

REM 1) Compile everything (including your org.json JAR on the classpath)
javac -cp .;%JSON_JAR% Master.java Worker.java Reducer.java AppManager.java AppClient.java
if errorlevel 1 (
  echo ***
  echo Compilation failed. Fix errors above.
  pause
  exit /b 1
)

REM 2) Start 3 Workers (only one argument: the port)
echo Starting Workers...
start "Worker6000" cmd /k "java -cp .;%JSON_JAR% Worker 6000"
start "Worker6001" cmd /k "java -cp .;%JSON_JAR% Worker 6001"
start "Worker6002" cmd /k "java -cp .;%JSON_JAR% Worker 6002"

REM 3) Start Reducer (no args)
echo Starting Reducer...
start "Reducer" cmd /k "java -cp .;%JSON_JAR% Reducer"

REM 4) Start Master (no args)
echo Starting Master...
start "Master" cmd /k "java -cp .;%JSON_JAR% Master"

REM 5) Start Manager UI (no args)
echo Starting AppManager...
start "AppManager" cmd /k "java -cp .;%JSON_JAR% AppManager"

REM 6) Start Client UI (no args)
echo Starting AppClient...
start "AppClient" cmd /k "java -cp .;%JSON_JAR% AppClient"

echo.
echo All processes launched.
pause
endlocal
