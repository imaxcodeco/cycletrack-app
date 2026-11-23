@echo off
setlocal
set "JAVA_HOME=C:\Users\Ian Max\.jdk17\jdk-17.0.21+9"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "C:\Users\Ian Max\cycletrack-app"
call gradlew.bat %* --console=plain
echo EXIT_CODE=%ERRORLEVEL%
