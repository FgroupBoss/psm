# 前端应用

## 应用拆分

| 应用 | 说明 |
| --- | --- |
| `apps/admin-web` | PC 管理端，覆盖系统管理、作业票、报警、承包商、重大危险源、报表 |
| `apps/mobile-site` | 移动端现场作业，覆盖待办、签到、检测、确认、拍照、签名、监护 |
| `apps/dashboard-web` | 中控大屏，覆盖风险态势、作业态势、报警态势、承包商态势 |

## 共享包

| 包 | 说明 |
| --- | --- |
| `packages/api-client` | API Client 和请求封装 |
| `packages/auth` | 登录态、SSO、权限、租户上下文 |
| `packages/ui` | 通用 UI 组件 |
| `packages/domain-types` | 业务类型定义 |
| `packages/utils` | 通用工具 |

## 技术栈

- TypeScript。
- Vite。
- React。
- npm workspaces。
- `admin-web` 提供 demo 页面，请求后端 `/api/demo`。

## 本地验证

```powershell
npm install
npm run typecheck
npm run dev:admin
```

`admin-web` 默认端口为 `5173`，并将 `/api` 代理到 `http://localhost:18081`。
