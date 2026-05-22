param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[a-z][a-z0-9-]*$')]
    [string]$Name
)

$root = Split-Path -Parent $PSScriptRoot
$serviceRoot = Join-Path $root "backend\services\$Name"

if (Test-Path $serviceRoot) {
    Write-Error "Backend service already exists: $serviceRoot"
    exit 1
}

$dirs = @(
    $serviceRoot,
    "$serviceRoot\src\main\java",
    "$serviceRoot\src\main\resources",
    "$serviceRoot\src\test\java"
)

foreach ($dir in $dirs) {
    New-Item -ItemType Directory -Path $dir -Force | Out-Null
}

@"
# $Name

后端微服务骨架。

职责：

- 待补充。

"@ | Set-Content -Path "$serviceRoot\README.md" -Encoding UTF8

New-Item -ItemType File -Path "$serviceRoot\src\main\java\.gitkeep" -Force | Out-Null
New-Item -ItemType File -Path "$serviceRoot\src\main\resources\.gitkeep" -Force | Out-Null
New-Item -ItemType File -Path "$serviceRoot\src\test\java\.gitkeep" -Force | Out-Null

Write-Host "Created backend service: $serviceRoot"

