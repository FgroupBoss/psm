# 审计 objectType（biz_type）约定

第 2 迭代批次 1 冻结的 `audit_change_log.biz_type` 取值，与 `com.fgroupboss.ai.psm.common.AuditBizType` 一致。

## 承包商（CONTRACTOR_*）

| biz_type | 说明 |
| --- | --- |
| `CONTRACTOR_COMPANY` | 承包商单位主档及状态变更 |
| `CONTRACTOR_QUALIFICATION` | 单位资质 |
| `CONTRACTOR_WORKER` | 承包商人员 |
| `CONTRACTOR_CERTIFICATE` | 人员证书 |
| `CONTRACTOR_TRAINING` | 培训记录 |
| `CONTRACTOR_VIOLATION` | 违章记录 |
| `CONTRACTOR_BLACKLIST` | 黑名单 |

## 重大危险源（MAJOR_HAZARD_*）

| biz_type | 说明 |
| --- | --- |
| `MAJOR_HAZARD` | 危险源主档及发布/状态 |
| `MAJOR_HAZARD_RESPONSIBILITY` | 包保责任人 |
| `MAJOR_HAZARD_POINT` | 监测点位绑定 |
| `MAJOR_HAZARD_ATTACHMENT` | 附件元数据 |

## 报警（ALARM_*）

| biz_type | 说明 |
| --- | --- |
| `ALARM` | 报警事件主档及状态变更 |
| `ALARM_ACTION` | 确认、派发、反馈、关闭等处置动作 |
| `ALARM_RULE` | 报警规则配置变更 |

## 使用说明

- 各业务服务写入审计时使用 {@link AuditBizType#code()}，禁止随意新增字符串。
- 承包商/危险源：本地流水 + **best-effort 双写** `psm-audit-service`（`CentralAuditClient`）。
- 管理端审计页筛选在批次 6（T2-X-602）已接入 `bizTypePrefix`。
