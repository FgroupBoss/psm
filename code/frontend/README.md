# 前端应用

## 后端对接（整合后 6 域架构）

前端全部通过 **网关**（`psm-identity-api` 内置，端口 `18080`）访问后端，不直连各域服务。

```
浏览器 → Vite Proxy (:5173/:5174/:5175) → Gateway (:18080) → 6 域服务
                                                    ├── psm-identity-api        (:18081) 身份与接入
                                                    ├── psm-operation-api       (:18088) 作业管控
                                                    ├── psm-realtime-api        (:18087) 实时感知
                                                    ├── psm-risk-api            (:18101) 风险防控
                                                    ├── psm-process-safety-api  (:18111) 过程安全
                                                    └── psm-incident-governance-api (:18115) 事件治理
```

## 应用拆分

| 应用 | 端口 | 说明 |
| --- | --- | --- |
| `apps/admin-web` | 5173 | PC 管理端：一期～三期全模块 |
| `apps/mobile-site` | 5174 | 移动端：作业票 / 巡检 / 隐患上报 |
| `apps/dashboard-web` | 5175 | 中控大屏：风险 / 作业 / 报警 / 承包商态势 |

## 共享包

| 包 | 说明 |
| --- | --- |
| `packages/api-client` | 统一 `request<T>()` + 文件上传，基于原生 fetch |
| `packages/auth` | Token 管理（localStorage），Bearer 认证 |
| `packages/domain-types` | 业务类型 + API 路径常量（按 6 域分组） |
| `packages/ui` | 通用 UI 组件 |
| `packages/utils` | 通用工具 |

## API 路径规范

所有路径在 `@psm/domain-types` 中统一定义，按领域分组：

| 域 | 常量前缀 | 示例 |
|----|---------|------|
| identity | `AUTH_`, `IAM_`, `AUDIT_`, `MASTER_DATA_`, `FILE_`, `NOTIFICATION_`, `CONFIG_` | `AUTH_API.login` |
| operation | `CONTRACTOR_`, `WORK_PERMIT_`, `MOBILE_`, `SIMOPS_` | `WORK_PERMIT_API.base` |
| realtime | `ALARM_`, `LOCATION_`, `VIDEO_` | `ALARM_API.base` |
| risk | `MAJOR_HAZARD_`, `DUAL_PREVENTION_`, `INSPECTION_` | `DUAL_PREVENTION_API.hazards` |
| process-safety | `PHA_`, `MOC_`, `PSSR_`, `BARRIER_` | `PHA_API.projects` |
| incident-gov | `INCIDENT_`, `GOVERNANCE_`, `REPORT_`, `INTEGRATION_REG_` | `REPORT_API.dashboardOverview` |

## 技术栈

- TypeScript 5.8
- Vite + React
- npm workspaces
- 原生 fetch（无 Axios）

## 本地验证

```powershell
npm install
npm run typecheck
npm run dev:admin      # → http://localhost:5173
npm run dev:mobile     # → http://localhost:5174
npm run dev:dashboard  # → http://localhost:5175
```

三个应用均将 `/api`、`/auth` 代理到网关 `http://localhost:18080`。
