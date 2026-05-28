# psm-integration-service

集成适配服务（端口 **18091**）。

## 职责

- HR/AD/LDAP、OA、GDS/DCS、门禁等外部系统对接（一期骨架）。
- **监管园区模块（M06）**：平台配置、字段/编码映射、业务数据抽取、上报任务、回执与对账。

## 监管模块（reg）

| 能力 | 说明 |
| --- | --- |
| 平台配置 | `/api/integration/reg/platforms` |
| 映射管理 | `/api/integration/reg/mappings` |
| 上报任务 | `/api/integration/reg/tasks`、`/tasks/trigger`、`/tasks/{id}/retry` |
| 预览/对账 | `/api/integration/reg/preview`、`/reconciliation` |

`RegBusinessDataExtractor` 通过 RestTemplate 从以下服务抽取待上报数据（`pageSize=100`）：

- 双重预防：`GET /api/dual-prevention/hazards`
- 作业票：`GET /api/work-permits`
- 重大危险源：`GET /api/major-hazards`
- 报警：`GET /api/alarms`

`RegReportScheduler` 每日 02:00（`0 0 2 * * ?`）为已启用平台触发四域定时上报。

## 配置

`application.yml` 中 `psm.*-service-url` 指向各业务服务基址（默认见文件内占位）。
