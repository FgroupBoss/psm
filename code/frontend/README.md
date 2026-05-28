# 前端应用

## 应用拆分

| 应用 | 说明 |
| --- | --- |
| `apps/admin-web` | PC 管理端：一期 + **二期**（双重预防、巡检、定位、视频、SIMOPS、监管、二期报表） |
| `apps/mobile-site` | 移动端：**待办/作业票** + **智能巡检** + **隐患上报** |
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

`admin-web` / `mobile-site` 默认端口 `5173` / `5174`，并将 `/api`、`/auth` 代理到网关 `http://localhost:18080`。
