$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectDir = Split-Path -Parent $scriptDir

$srcDir = Join-Path $projectDir "src"
$binDir = Join-Path $projectDir "bin"

if (-not (Test-Path -LiteralPath $binDir)) {
    New-Item -ItemType Directory -Path $binDir | Out-Null
}

Write-Host "Compiling Cafe Tycoon..."

$sources = Get-ChildItem -LiteralPath $srcDir -Filter "*.java" -Recurse
$sourceFile = Join-Path $env:TEMP "cafe_sources.txt"

# Format paths with forward slashes and quotes to avoid javac spaces and escape character issues
$sourceList = $sources | ForEach-Object { '"{0}"' -f $_.FullName.Replace('\', '/') }
$sourceList | Set-Content -LiteralPath $sourceFile -Encoding ASCII

# Run javac
$javacArgs = @("-encoding", "UTF-8", "-d", $binDir, "@$sourceFile")
& javac $javacArgs

$err = $LASTEXITCODE
Remove-Item $sourceFile -ErrorAction SilentlyContinue

if ($err -ne 0) {
    Write-Host "Compilation failed!"
    exit 1
}

Write-Host "Starting game..."
Set-Location -LiteralPath $projectDir
& java -cp bin main.Main
