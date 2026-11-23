Write-Host "Script de download de imagens para CycleTrack" -ForegroundColor Cyan
$imageDir = "app\src\main\res\drawable"
if (-not (Test-Path $imageDir)) {
    New-Item -ItemType Directory -Path $imageDir -Force | Out-Null
}
Write-Host "Diretório: $imageDir" -ForegroundColor Green
Write-Host "Imagens serão baixadas do Unsplash quando necessário" -ForegroundColor Yellow
