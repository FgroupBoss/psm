# 脚本目录

脚本用于按需生成后端服务、前端应用和前端共享包目录。

PowerShell 示例：

```powershell
.\scripts\New-BackendService.ps1 -Name psm-example-service
.\scripts\New-FrontendApp.ps1 -Name example-web
.\scripts\New-FrontendPackage.ps1 -Name example-package
```

脚本只生成目录和占位文件，不生成业务实现。

