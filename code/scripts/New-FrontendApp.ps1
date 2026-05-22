param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[a-z][a-z0-9-]*$')]
    [string]$Name
)

$root = Split-Path -Parent $PSScriptRoot
$appRoot = Join-Path $root "frontend\apps\$Name"

if (Test-Path $appRoot) {
    Write-Error "Frontend app already exists: $appRoot"
    exit 1
}

New-Item -ItemType Directory -Path "$appRoot\src" -Force | Out-Null

@"
# $Name

前端应用骨架。

职责：

- 待补充。

"@ | Set-Content -Path "$appRoot\README.md" -Encoding UTF8

New-Item -ItemType File -Path "$appRoot\src\.gitkeep" -Force | Out-Null

Write-Host "Created frontend app: $appRoot"

