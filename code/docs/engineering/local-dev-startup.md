# 本地联调启动说明

> 适用：一期全量联调 + **二期 API、管理端/移动端页面、多服务 E2E 与 UAT 签收**；当前覆盖承包商、危险源、报警、作业票、移动现场、报表大屏、文件与通知服务，以及双重预防、巡检、定位、视频、SIMOPS、监管扩展。

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
| `psm_notification` | notification |
| `psm_file` | file |
| `psm_work_permit` | work-permit |
| `psm_report` | report |
| `psm_dual_prevention` | dual-prevention |
| `psm_inspection` | inspection |
| `psm_location` | location |
| `psm_video` | video |
| `psm_integration` | integration（监管上报） |

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
| `psm-work-permit-service` | 18088 | 危险工作票 |
| `psm-mobile-bff` | 18089 | 移动 BFF |
| `psm-report-service` | 18090 | 报表/大屏 |
| `psm-notification-service` | 18093 | 消息中心（站内信） |
| `psm-file-service` | 18092 | 文件中心 |
| `psm-audit-service` | 18094 | 中央审计查询 |
| `psm-dual-prevention-service` | 18101 | 双重预防与隐患 |
| `psm-inspection-service` | 18102 | 智能巡检 |
| `psm-location-service` | 18103 | 人员定位与封闭化 |
| `psm-video-service` | 18104 | 视频 AI 与过程监护 |
| `psm-integration-service` | 18091 | 监管园区接口 |

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
mvn -pl services/psm-work-permit-service -am spring-boot:run
mvn -pl services/psm-mobile-bff -am spring-boot:run
mvn -pl services/psm-report-service -am spring-boot:run
mvn -pl services/psm-file-service -am spring-boot:run
mvn -pl services/psm-notification-service -am spring-boot:run
mvn -pl services/psm-dual-prevention-service -am spring-boot:run
mvn -pl services/psm-inspection-service -am spring-boot:run
mvn -pl services/psm-location-service -am spring-boot:run
mvn -pl services/psm-video-service -am spring-boot:run
mvn -pl services/psm-integration-service -am spring-boot:run
mvn -pl services/psm-gateway -am spring-boot:run
```

前端（`code/frontend`）：

```powershell
npm install
npm run dev:admin
npm run dev:mobile
npm run dev:dashboard
```

访问入口：

- 管理端：`http://localhost:5173`，Vite 代理到网关 `18080`。
- 移动端：`http://localhost:5174`。
- 大屏端：`http://localhost:5175`。

## 试点账号

以 `psm-auth-service` / `psm-iam-service` 种子数据为准（默认租户 `tenantId=1`）。

## 审计双写

承包商/危险源业务操作会：

1. 写各自库本地流水（`contractor_audit_record` / `major_hazard_audit_record`）
2. **best-effort** 上报 `psm-audit-service` 的 `audit_change_log`（`psm.central-audit-enabled=true`）

- 管理端「审计日志」页按 `bizTypePrefix` 筛选 `CONTRACTOR_*`、`MAJOR_HAZARD*`、`ALARM*` 及二期 `DUAL_PREVENTION_*`、`INSPECTION_*`、`LOCATION_*`、`VIDEO_*`、`SIMOPS_*`、`REG_*`。

报警处置（confirm/dispatch/close 等）会双写 `alarm_action_record` 与中央审计 `ALARM_ACTION` / `ALARM`。

## 报警服务联调要点

- 去重：同源同等级同区域 5 分钟内 ingest 合并，`occurrence_count` 递增。
- 升级：`@Scheduled` 每分钟扫描 NEW/CONFIRMED/IN_PROGRESS 超时规则（种子见 `psm_alarm` data.sql）。
- 区域阻断：`POST /api/alarms/area-active-check`；major-hazard `risk-context` 默认查询并设置 `blockingAlarm`。
- 危险源 Tab：`GET /api/major-hazards/{id}/alarms` 代理 alarm 列表（按 hazardId 筛选）。

## 二期联调签收要点

- 网关 `18080` 已转发 `/api/dual-prevention`、`/api/inspection`、`/api/location`、`/api/video`、`/api/simops`、`/api/integration/reg/*`，并完成二期多服务 E2E 签收。
- 重大危险源绑定巡检：`POST /api/major-hazards/{id}/inspection-plan/bind`；查询计划 `GET /api/inspection/plans/by-major-hazard`；最近任务 `GET /api/inspection/tasks/by-major-hazard`。
- 隐患逾期/升级通知模板：`HAZARD_OVERDUE`、`HAZARD_ESCALATION`（`psm-notification-service` 内置）。
- 二期业务操作 best-effort 双写中央审计（见 [audit-object-types.md](./audit-object-types.md) 二期 biz_type）。
- 监管上报传输按项目监管平台配置启用；本地与无外部平台环境保留 mock 回执作为开发/演示降级能力，现场联调签收以监管平台实际回执为准。

## M06～M08 联调要点

- 作业票：动火/受限空间创建、审批、许可、暂停恢复、验收、归档全流程已完成 AC-M06 验收。
- 移动现场：待办、签到、气体检测、措施确认、拍照/签名、许可、监护、弱网草稿、报警反馈已完成 AC-M07 验收。
- 报表大屏：作业、报警、危险源、承包商、审计统计、明细下钻、导出、大屏和 UAT 记录已完成 AC-M08 验收。

## 验收脚本

- 构建：`mvn -DskipTests=false test`；`npm run typecheck && npm run build`
- 用例清单见 [模块功能闭环索引](../../../design/01_一期落地设计/00_开发总览/迭代/00_模块功能闭环索引.md)
