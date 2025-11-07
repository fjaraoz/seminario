@echo off
setlocal

rem Ir a la carpeta donde está este .bat
cd /d "%~dp0"

rem === OPCIONAL: si ya tenés Java en el PATH, podés comentar estas 2 líneas ===
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"

rem Crear carpeta bin si no existe
if not exist bin mkdir bin

echo Generando lista de fuentes...
rem Lista absoluta de TODOS los .java bajo src
dir /s /b src\*.java > sources.txt

echo Compilando clases...
javac -encoding UTF-8 -d bin -cp "lib/*" @sources.txt

if errorlevel 1 (
    echo.
    echo Hubo errores de compilacion. Revisalos arriba.
    echo.
    pause
    exit /b 1
)

echo.
echo Ejecutando aplicacion...
java -Dfile.encoding=UTF-8 -cp "bin;lib/*" app.Main

echo.
echo Presiona una tecla para salir...
pause >nul
endlocal
