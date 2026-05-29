# psm-incident-service

事故调查与根因分析微服务。端口 **18115**，数据库 `psm_incident`。

## 启动

```bash
cd code/backend/services/psm-incident-service
mvn spring-boot:run
```

环境变量（可选）：

- `PSM_INCIDENT_SERVICE_DB_URL`
- `PSM_INCIDENT_SERVICE_DB_USERNAME`
- `PSM_INCIDENT_SERVICE_DB_PASSWORD`

建表脚本：`src/main/resources/db/schema.sql`

## API（前缀 `/api/incidents`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/incidents` | 事件台账分页（`tenantId` 必填） |
| POST | `/api/incidents` | 新建事件报告 |
| GET | `/api/incidents/{id}` | 事件详情 |
| PUT | `/api/incidents/{id}` | 更新事件 |
| POST | `/api/incidents/from-source` | 外部来源转事件 |
| POST | `/api/incidents/{id}/start-investigation` | 调查立项 |
| GET/POST | `/api/incidents/{id}/timeline` | 时间线 |
| GET/POST | `/api/incidents/{id}/evidence` | 证据 |
| GET/POST | `/api/incidents/{id}/root-causes` | 根因 |
| GET/POST | `/api/incidents/{id}/capa` | CAPA |
| POST | `/api/incidents/capa/{id}/verify` | CAPA 验证 |
| POST | `/api/incidents/{id}/lessons-learned` | 经验反馈 |

统一返回 `ResponseVO`；业务错误码见 `BusinessException`。

## 状态机

- **事件**：`REPORTED` → `INVESTIGATING`（立项）→ `CAPA_EXECUTING`（创建 CAPA）等，见 `IncidentStatus`
- **CAPA**：`EXECUTING` → `CLOSED` / `RETURNED`（验证），见 `CapaStatus`

## 测试

```bash
mvn -DskipTests=false test
```
