param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[a-z][a-z0-9-]*$')]
    [string]$Name
)

$root = Split-Path -Parent $PSScriptRoot
$packageRoot = Join-Path $root "frontend\packages\$Name"

if (Test-Path $packageRoot) {
    Write-Error "Frontend package already exists: $packageRoot"
    exit 1
}

New-Item -ItemType Directory -Path "$packageRoot\src" -Force | Out-Null

@"
# $Name

前端共享包骨架。

职责：

- 待补充。

"@ | Set-Content -Path "$packageRoot\README.md" -Encoding UTF8

New-Item -ItemType File -Path "$packageRoot\src\.gitkeep" -Force | Out-Null

Write-Host "Created frontend package: $packageRoot"

