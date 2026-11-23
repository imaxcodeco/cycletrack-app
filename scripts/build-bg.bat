@echo off
REM Build em background: grava log em build-log.txt e o PID em build.pid
cd /d "C:\Users\Ian Max\cycletrack-app"
del /q build-log.txt 2>nul
start "cycletrack-build" /min cmd /c "scripts\gradlew17.bat :app:assembleDebug > build-log.txt 2>&1 & echo EXIT_CODE=%ERRORLEVEL% >> build-log.txt"
echo BUILD_STARTED
