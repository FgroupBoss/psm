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

@"
{
  "name": "@psm/$Name",
  "version": "1.0.0",
  "private": true,
  "type": "module",
  "main": "src/index.ts",
  "types": "src/index.ts"
}
"@ | Set-Content -Path "$packageRoot\package.json" -Encoding UTF8

@"
{
  "extends": "../../tsconfig.base.json",
  "compilerOptions": {
    "composite": true,
    "declaration": true,
    "emitDeclarationOnly": true,
    "declarationMap": true
  },
  "include": ["src"]
}
"@ | Set-Content -Path "$packageRoot\tsconfig.json" -Encoding UTF8

@"
export const packageName = '@psm/$Name';
"@ | Set-Content -Path "$packageRoot\src\index.ts" -Encoding UTF8

Write-Host "Created frontend package: $packageRoot"
