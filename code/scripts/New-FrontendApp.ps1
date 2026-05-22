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

@"
{
  "name": "@psm/$Name",
  "version": "1.0.0",
  "private": true,
  "type": "module"
}
"@ | Set-Content -Path "$appRoot\package.json" -Encoding UTF8

@"
{
  "extends": "../../tsconfig.base.json",
  "compilerOptions": {
    "composite": true,
    "noEmit": true
  },
  "include": ["src", "vite.config.ts"]
}
"@ | Set-Content -Path "$appRoot\tsconfig.json" -Encoding UTF8

@"
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 0
  }
});
"@ | Set-Content -Path "$appRoot\vite.config.ts" -Encoding UTF8

@"
<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>$Name</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
"@ | Set-Content -Path "$appRoot\index.html" -Encoding UTF8

@"
import React from 'react';
import { createRoot } from 'react-dom/client';

function App() {
  return <main>$Name scaffold</main>;
}

createRoot(document.getElementById('root') as HTMLElement).render(<App />);
"@ | Set-Content -Path "$appRoot\src\main.tsx" -Encoding UTF8

Write-Host "Created frontend app: $appRoot"
