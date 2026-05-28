# psm-dual-prevention-service

双重预防与隐患治理服务（端口 **18101**）。

## 能力

- 风险单元、风险事件、管控措施与风险清单。
- 隐患上报、确认、整改、复查、逾期检查与统计。

## 核心 API

- `/api/dual-prevention/risk-units`
- `/api/dual-prevention/hazards`（含 `/statistics`）
- `/api/dual-prevention/hazards/overdue-check`（供作业票规则调用）

设计文档：[M01 双重预防与隐患治理](../../../../design/02_二期落地设计/00_开发总览/迭代/M01_双重预防与隐患治理功能闭环.md)
