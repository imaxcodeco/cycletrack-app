@echo off
REM ============================================================================
REM CycleTrack - Keystore Generation Script
REM ============================================================================
REM This script generates a release keystore for signing the Android app.
REM Run this ONCE and store the keystore file securely!
REM ============================================================================

setlocal enabledelayedexpansion

echo.
echo ============================================================================
echo CycleTrack Release Keystore Generator
echo ============================================================================
echo.

REM Configuration - CHANGE THESE VALUES
set KEYSTORE_PATH=..\keystore\release.keystore
set KEY_ALIAS=cycletrack-release-key
set KEY_PASSWORD=CHANGE_THIS_TO_STRONG_PASSWORD
set STORE_PASSWORD=CHANGE_THIS_TO_STRONG_PASSWORD
set VALIDITY_DAYS=10000
set KEY_SIZE=2048
set KEY_ALG=RSA

REM Distinguished Name (DN) - CHANGE THESE VALUES
set DN_CN="CycleTrack App"
set DN_OU="Mobile Development"
set DN_O="CycleTrack"
set DN_L="Sao Paulo"
set DN_ST="SP"
set DN_C="BR"

echo This will generate a keystore at: %KEYSTORE_PATH%
echo Key alias: %KEY_ALIAS%
echo Validity: %VALIDITY_DAYS% days (~27 years)
echo.
echo IMPORTANT: You MUST change the default passwords above before running!
echo.

set /p CONFIRM="Have you updated the passwords and DN info? (y/N): "
if /i not "%CONFIRM%"=="y" (
    echo Aborted. Please edit this file and update the configuration.
    pause
    exit /b 1
)

REM Create keystore directory if it doesn't exist
if not exist "..\keystore" mkdir "..\keystore"

echo.
echo Generating keystore...
echo.

keytool -genkeypair ^
    -alias %KEY_ALIAS% ^
    -keyalg %KEY_ALG% ^
    -keysize %KEY_SIZE% ^
    -validity %VALIDITY_DAYS% ^
    -keystore %KEYSTORE_PATH% ^
    -storepass %STORE_PASSWORD% ^
    -keypass %KEY_PASSWORD% ^
    -dname "CN=%DN_CN%, OU=%DN_OU%, O=%DN_O%, L=%DN_L%, ST=%DN_ST%, C=%DN_C%" ^
    -ext "SAN=dns:cycletrack.app,dns:www.cycletrack.app,ip:127.0.0.1"

if errorlevel 1 (
    echo.
    echo ERROR: Failed to generate keystore!
    pause
    exit /b 1
)

echo.
echo ============================================================================
echo KEYSTORE GENERATED SUCCESSFULLY!
echo ============================================================================
echo.
echo Keystore location: %KEYSTORE_PATH%
echo Key alias: %KEY_ALIAS%
echo.
echo IMPORTANT: Save these credentials SECURELY (password manager, encrypted file):
echo   - Keystore password: %STORE_PASSWORD%
echo   - Key password: %KEY_PASSWORD%
echo   - Key alias: %KEY_ALIAS%
echo.
echo Add to your CI/CD secrets (GitHub Actions, etc.):
echo   - SIGNING_STORE_FILE: (base64 encoded keystore file)
echo   - SIGNING_STORE_PASSWORD: %STORE_PASSWORD%
echo   - SIGNING_KEY_ALIAS: %KEY_ALIAS%
echo   - SIGNING_KEY_PASSWORD: %KEY_PASSWORD%
echo.
echo Also save to local.properties for local builds:
echo   KEYSTORE_PATH=../keystore/release.keystore
echo   KEYSTORE_PASSWORD=%STORE_PASSWORD%
echo   KEY_ALIAS=%KEY_ALIAS%
echo   KEY_PASSWORD=%KEY_PASSWORD%
echo.
echo WARNING: Never commit the keystore file or passwords to version control!
echo ============================================================================
echo.

pause