# M02 · MOC 变更管理功能闭环 ⬜

> **签收**：未签收 · **批次**：0/5  
> **所属迭代**：第 2 迭代 · MOC 变更管理闭环  
> **落地设计**：[02_MOC变更管理/落地设计.md](../../02_MOC变更管理/落地设计.md) · 服务 `psm-moc-service` :18112

## 1. 目标与边界

**Must**：变更申请、分类分级、影响分析、审批、实施任务、验证关闭、PHA/PSSR/培训/文件更新触发。

**Won't**：替代 OA 全流程平台、自动生成工艺包、自动下发控制系统参数。

## 2. 功能闭环

```text
提交变更申请
 -> 分类分级和初审
 -> 影响分析和风险评估
 -> 动态审批
 -> 实施任务和现场验证
 -> 完成培训/文件/PSSR 等关闭条件
 -> 变更关闭和审计归档
```

## 3. 依赖与输出

| 能力 | 用法 |
| --- | --- |
| 主数据 | 装置、设备、人员、组织 |
| PHA/LOPA | 高风险变更触发分析或复审 |
| PSSR | 开车前验证触发 |
| 输出 | 变更任务、影响分析、关闭条件、文件更新清单 |

## 4. 核心 API

`/api/moc/changes` · `/api/moc/changes/{id}/submit` · `/api/moc/changes/{id}/impact-analysis` · `/api/moc/changes/{id}/approve` · `/api/moc/changes/{id}/implementation-tasks` · `/api/moc/changes/{id}/verify` · `/api/moc/changes/{id}/close`

## 5. 数据对象

`moc_change`、`moc_impact_analysis`、`moc_approval_record`、`moc_implementation_task`、`moc_close_condition`、`moc_document_update`、`moc_training_requirement`

## 6. 批次摘要

| 批次 | 内容 | 结果 |
| --- | --- | --- |
| 1 | 变更申请、分类分级 | ⬜ |
| 2 | 影响分析和任务派发 | ⬜ |
| 3 | 动态审批和实施计划 | ⬜ |
| 4 | PHA/PSSR/培训/文件更新触发 | ⬜ |
| 5 | 验证关闭、审计和 E2E | ⬜ |

## 7. 验收 AC-M02

| 编号 | 检查项 | 状态 |
| --- | --- | --- |
| AC-M02-01 | 变更可按类型、等级、装置和设备提交 | ⬜ |
| AC-M02-02 | 影响分析可覆盖工艺、设备、仪表、安全、人员、文件 | ⬜ |
| AC-M02-03 | 高风险变更可触发 PHA/PSSR | ⬜ |
| AC-M02-04 | 关闭前必须完成实施验证和指定关闭条件 | ⬜ |
| AC-M02-05 | 全流程操作可审计追溯 | ⬜ |

## 8. 遗留

| 项 | 计划 | 说明 |
| --- | --- | --- |
| 与 OA 深度流转 | 项目适配 | 三期先保留审批接口和回调扩展点 |

## 9. 维护

签收或批次完成 -> 更新本文件验收段 + [项目进展总览.md](../项目进展总览.md)。
