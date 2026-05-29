# psm-governance-service

集团治理与数据仓库微服务。端口 **18116**，数据库 `psm_governance`。

## 启动

```bash
mvn -pl services/psm-governance-service -am spring-boot:run
```

环境变量（可选）：

- `PSM_GOVERNANCE_SERVICE_DB_URL`
- `PSM_GOVERNANCE_SERVICE_DB_USERNAME`
- `PSM_GOVERNANCE_SERVICE_DB_PASSWORD`

## API（前缀 `/api/governance`）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/templates` | 集团模板分页 |
| POST | `/templates` | 创建模板（含 v1 草稿版本） |
| POST | `/templates/{id}/publish` | 发布模板版本（状态 `PUBLISHED`） |
| GET | `/sites` | 基地映射分页 |
| POST | `/sites` | 创建基地映射 |
| GET | `/metrics` | 指标定义分页 |
| POST | `/metrics` | 创建指标定义 |
| GET | `/metrics/snapshots` | 指标快照查询 |
| GET | `/audit-issues` | 审计问题分页 |
| POST | `/audit-issues` | 创建审计问题 |
| GET | `/dashboard/overview` | 驾驶舱：按基地汇总 DW 事实表计数 |
| GET | `/benchmark` | 跨基地指标对标（基于快照） |

## 测试

```bash
mvn -pl services/psm-governance-service -DskipTests=false test
```

## 包结构

- `com.fgroupboss.ai.psm.governance` — 启动类
- `controller` — REST 与全局异常
- `config` — 模板版本状态机
- `service` / `service.impl` — 业务实现
- `mapper` / `model.entity` — MyBatis-Plus
- `model.dto` / `model.vo` — 入参与出参
