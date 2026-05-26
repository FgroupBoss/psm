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

## 使用说明

- 各业务服务写入审计时使用 {@link AuditBizType#code()}，禁止随意新增字符串。
- 管理端审计页筛选在批次 6（T2-X-602）接入上述类型。
