@echo off
REM ============================================================================
REM CycleTrack - Build com JDK 17 (versao suportada por AGP 8.6 + Kotlin 1.9.22)
REM ============================================================================

setlocal

set "PROJECT_DIR=C:\Users\Ian Max\cycletrack-app"
set "JAVA_HOME=C:\Users\Ian Max\.jdk17\jdk-17.0.21+9"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%PROJECT_DIR%"

echo ============================================================================
echo CycleTrack Build - %*
echo JAVA_HOME: %JAVA_HOME%
echo ============================================================================
echo.

call "%PROJECT_DIR%\gradlew.bat" %* --console=plain
set EXIT_CODE=%ERRORLEVEL%

echo.
echo ============================================================================
if %EXIT_CODE%==0 (
    echo BUILD SUCESSO ^(codigo %EXIT_CODE%^)
) else (
    echo BUILD FALHOU ^(codigo %EXIT_CODE%^)
)
echo ============================================================================

exit /b %EXIT_CODE%
