# M01 · PHA/HAZOP 与 LOPA 功能闭环 ⬜

> **签收**：未签收 · **批次**：0/5  
> **所属迭代**：第 1 迭代 · PHA/HAZOP 与 LOPA 基础  
> **落地设计**：[01_PHA-HAZOP与LOPA/落地设计.md](../../01_PHA-HAZOP与LOPA/落地设计.md) · 服务 `psm-pha-service` :18111

## 1. 目标与边界

**Must**：PHA 项目、HAZOP 节点、偏差、原因、后果、保护层、建议项、LOPA 场景、IPL、风险计算、复审计划。

**Won't**：复杂图形化协同编辑器、AI 自动生成专家结论、SIL 验证报告自动签发。

## 2. 功能闭环

```text
创建 PHA 项目
 -> 维护节点和团队
 -> 执行 HAZOP 偏差分析
 -> 形成建议项并分派整改
 -> 对高风险场景执行 LOPA
 -> 验证建议项关闭
 -> 生成复审计划和审计归档
```

## 3. 依赖与输出

| 能力 | 用法 |
| --- | --- |
| 主数据 | 区域、装置、设备、人员、组织 |
| 重大危险源 | 关联 PHA 项目和分析节点 |
| 双重预防 | 风险单元和风险事件作为分析输入 |
| 输出 | 建议项、LOPA 场景、SIL 建议、复审计划 |

## 4. 核心 API

`/api/pha/projects` · `/api/pha/projects/{id}/nodes` · `/api/pha/nodes/{id}/deviations` · `/api/pha/recommendations` · `/api/pha/recommendations/{id}/close` · `/api/pha/lopa-scenarios` · `/api/pha/lopa-scenarios/{id}/calculate` · `/api/pha/projects/{id}/report`

## 5. 数据对象

`pha_project`、`pha_node`、`hazop_deviation`、`hazop_cause`、`hazop_consequence`、`hazop_safeguard`、`pha_recommendation`、`lopa_scenario`、`lopa_ipl`、`pha_review_plan`

## 6. 批次摘要

| 批次 | 内容 | 结果 |
| --- | --- | --- |
| 1 | PHA 项目、节点、团队和会议记录 | ⬜ |
| 2 | HAZOP 偏差分析表 | ⬜ |
| 3 | 建议项状态机和整改验证 | ⬜ |
| 4 | LOPA 场景、IPL、风险计算 | ⬜ |
| 5 | 与危险源、屏障、MOC 联调 | ⬜ |

## 7. 验收 AC-M01

| 编号 | 检查项 | 状态 |
| --- | --- | --- |
| AC-M01-01 | PHA 项目可绑定装置、节点、团队和复审周期 | ⬜ |
| AC-M01-02 | HAZOP 分析表可维护偏差、原因、后果和保护层 | ⬜ |
| AC-M01-03 | 建议项可分派、整改、验证和关闭 | ⬜ |
| AC-M01-04 | LOPA 可基于 IPL 计算目标风险差距 | ⬜ |
| AC-M01-05 | 报告可导出并保留版本快照 | ⬜ |

## 8. 遗留

| 项 | 计划 | 说明 |
| --- | --- | --- |
| HAZOP 图形协同 | 后置 | 三期先支持结构化表格和导入导出 |

## 9. 维护

签收或批次完成 -> 更新本文件验收段 + [项目进展总览.md](../项目进展总览.md)。
