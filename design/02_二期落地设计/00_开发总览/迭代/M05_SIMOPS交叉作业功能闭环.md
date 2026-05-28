# M05 · SIMOPS 交叉作业功能闭环 🔄

> **签收**：— · **批次**：3/4（API 闭环 ✅，联调 E2E ⬜）  
> **所属迭代**：第 5 迭代 · SIMOPS 与作业联动  
> **落地设计**：[05_SIMOPS交叉作业/落地设计.md](../../05_SIMOPS交叉作业/落地设计.md) · 扩展 `psm-work-permit-service` + `psm-config-rule-service`

## 1. 目标与边界

**Must**：冲突矩阵配置、区域时间重叠扫描、WARN/COORDINATE/BLOCK 策略、协调审批、冲突审计和统计。

**Won't**：三维空间冲突、管线物理碰撞分析、自动排程优化。

## 2. 功能闭环

```text
配置冲突矩阵
 -> 作业票提交/许可前扫描
 -> 识别同区域重叠作业
 -> 提示/协调/阻断
 -> 协调审批（如需）
 -> 记录冲突快照
 -> 统计复盘
```

## 3. 依赖与输出

| 能力 | 用法 |
| --- | --- |
| 作业票 | 提交/许可环节调用扫描 |
| 配置规则 | 冲突矩阵和策略存储 |
| 输出 | 统一规则返回结构供 Web/移动展示 |

## 4. 核心 API

`/api/simops/rules` · `/api/simops/scan` · `/api/simops/conflicts` · `/api/simops/conflicts/{id}/coordinate` · `/api/simops/statistics`

## 5. 数据对象

`simops_conflict_rule`、`simops_scan_result`、`simops_conflict_item`、`simops_coordination_record`

## 6. 批次摘要

| 批次 | 内容 | 结果 |
| --- | --- | --- |
| 1 | 冲突矩阵配置和扫描引擎 | ✅ API |
| 2 | 作业票提交/许可集成 | ✅ API |
| 3 | 协调审批流程 | ✅ API |
| 4 | 冲突台账、统计、E2E | 🔄 API ✅ · E2E ⬜ |

## 7. 验收 AC-M05

| 编号 | 检查项 | 状态 |
| --- | --- | --- |
| AC-M05-01 | 冲突矩阵可配置 | ✅ API |
| AC-M05-02 | 同区域重叠作业自动识别 | ✅ API |
| AC-M05-03 | BLOCK 策略阻断提交/许可 | ✅ API |
| AC-M05-04 | COORDINATE 策略协调审批 | ✅ API |
| AC-M05-05 | 冲突记录可审计和统计 | ✅ API |
| AC-M05-06 | mvn test + 作业票 E2E | ⬜ 联调 |

## 8. 维护

签收或批次完成 → 更新本文件验收段 + [项目进展总览.md](../项目进展总览.md)。
