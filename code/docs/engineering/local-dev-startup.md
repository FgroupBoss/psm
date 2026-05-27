# 本地联调启动说明

> 适用：一期多迭代联调；当前含第 2 迭代（承包商/危险源）与第 3 迭代（报警闭环，已签收）。

## 前置条件

- JDK 8、Maven 3.6+
- Node.js 18+（前端）
- MySQL 8（本地或 Docker），账号默认 `psm` / `psm`
- 各服务库需先执行对应 `src/main/resources/db/schema.sql` 与 `data.sql`

## 数据库（建议）

| 库名 | 服务 |
| --- | --- |
| `psm_auth` | auth |
| `psm_iam` | iam |
| `psm_master_data` | master-data |
| `psm_config_rule` | config-rule |
| `psm_audit` | audit |
| `psm_contractor` | contractor |
| `psm_major_hazard` | major-hazard |
| `psm_alarm` | alarm |

## 服务端口

| 服务 | 端口 | 说明 |
| --- | --- | --- |
| `psm-gateway` | 18080 | 统一入口，管理端经此访问 |
| `psm-auth-service` | 18081 | 登录 |
| `psm-iam-service` | 18082 | 菜单/权限 |
| `psm-master-data-service` | 18083 | 主数据 |
| `psm-config-rule-service` | 18084 | 字典/规则 |
| `psm-contractor-service` | 18085 | 承包商 |
| `psm-major-hazard-service` | 18086 | 重大危险源 |
| `psm-alarm-service` | 18087 | 报警中心 |
| `psm-file-service` | 18092 | 文件中心（骨架，附件可 mock fileId） |
| `psm-audit-service` | 18094 | 中央审计查询 |

## 启动顺序（建议）

在 `code/backend` 目录分别开终端：

```powershell
mvn -pl services/psm-auth-service -am spring-boot:run
mvn -pl services/psm-iam-service -am spring-boot:run
mvn -pl services/psm-master-data-service -am spring-boot:run
mvn -pl services/psm-config-rule-service -am spring-boot:run
mvn -pl services/psm-audit-service -am spring-boot:run
mvn -pl services/psm-contractor-service -am spring-boot:run
mvn -pl services/psm-major-hazard-service -am spring-boot:run
mvn -pl services/psm-alarm-service -am spring-boot:run
mvn -pl services/psm-gateway -am spring-boot:run
```

前端（`code/frontend`）：

```powershell
npm install
npm run dev:admin
```

访问 `http://localhost:5173`，Vite 代理到网关 `18080`。

## 试点账号

以 `psm-auth-service` / `psm-iam-service` 种子数据为准（默认租户 `tenantId=1`）。

## 审计双写

承包商/危险源业务操作会：

1. 写各自库本地流水（`contractor_audit_record` / `major_hazard_audit_record`）
2. **best-effort** 上报 `psm-audit-service` 的 `audit_change_log`（`psm.central-audit-enabled=true`）

管理端「审计日志」页按 `bizTypePrefix` 筛选 `CONTRACTOR_*`、`MAJOR_HAZARD*`、`ALARM*`。

报警处置（confirm/dispatch/close 等）会双写 `alarm_action_record` 与中央审计 `ALARM_ACTION` / `ALARM`。

## 报警服务联调要点

- 去重：同源同等级同区域 5 分钟内 ingest 合并，`occurrence_count` 递增。
- 升级：`@Scheduled` 每分钟扫描 NEW/CONFIRMED/IN_PROGRESS 超时规则（种子见 `psm_alarm` data.sql）。
- 区域阻断：`POST /api/alarms/area-active-check`；major-hazard `risk-context` 默认查询并设置 `blockingAlarm`。
- 危险源 Tab：`GET /api/major-hazards/{id}/alarms` 代理 alarm 列表（按 hazardId 筛选）。

## 验收脚本

- 构建：`mvn -DskipTests=false test`；`npm run typecheck && npm run build`
- 用例清单见 [第 2 迭代档案](../../../design/10_一期落地设计/00_开发总览/迭代/02_准入与风险对象.md)
