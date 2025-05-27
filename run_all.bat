@echo off
setlocal


set JSON_SRC=C:\Users\super\Downloads\3210259_3210112_3110171


echo Compiling org.json sources...
javac -d . "%JSON_SRC%\org\json\*.java"
if errorlevel 1 (
  echo *** Failed to compile org.json from %JSON_SRC%\org\json ***
  pause
  exit /b 1
)


echo Compiling project sources...
javac -cp . Master.java Worker.java Reducer.java AppManager.java AppClient.java
if errorlevel 1 (
  echo *** Project compilation failed. Fix errors above. ***
  pause
  exit /b 1
)


echo Starting Workers...
start "Worker6000" cmd /k "java -cp . Worker 6000"
start "Worker6001" cmd /k "java -cp . Worker 6001"
start "Worker6002" cmd /k "java -cp . Worker 6002"


echo Starting Reducer...
start "Reducer" cmd /k "java -cp . Reducer"


echo Starting Master...
start "Master" cmd /k ^
  "java -cp . Master ^
    5000 ^
    127.0.0.1:7000 ^
    127.0.0.1:6000 ^
    127.0.0.1:6001 ^
    127.0.0.1:6002"


echo Starting Manager UI...
start "AppManager" cmd /k "java -cp . AppManager"

echo Starting Client UI...
start "AppClient" cmd /k "java -cp . AppClient"

echo.
echo All processes launched.
pause
endlocal
