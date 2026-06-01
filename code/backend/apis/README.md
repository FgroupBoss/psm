# 领域服务

**Layer 2** — 6 个完整的 Spring Boot 领域服务，每个同时包含 API 契约（DTO）和完整业务实现。

```
apis/
├── psm-identity-api/              ← 身份与接入域
├── psm-operation-api/             ← 作业管控域
├── psm-realtime-api/              ← 实时感知域
├── psm-risk-api/                  ← 风险防控域
├── psm-process-safety-api/        ← 过程安全域
└── psm-incident-governance-api/   ← 事件治理域
```

## 领域详情

| 模块 | 合并来源 | Java文件 | 子包 |
|------|---------|---------|------|
| **psm-identity-api** | gateway + auth + iam + audit + file + notification + master-data + identity | ~136 | gateway, auth, iam, audit, file, notification, masterdata, config |
| **psm-operation-api** | work-permit + contractor + mobile-bff + operation-control | ~164 | workpermit, contractor, mobile, config |
| **psm-realtime-api** | alarm + location + video + realtime-perception | ~160 | alarm, location, video, config |
| **psm-risk-api** | major-hazard + inspection + dual-prevention + risk-control | ~165 | majorhazard, inspection, dualprevention, config |
| **psm-process-safety-api** | pha + moc + pssr + barrier + config-rule + process-safety | ~190 | pha, moc, pssr, barrier, configrule, config |
| **psm-incident-governance-api** | incident + governance + report + integration + incident-governance | ~189 | incident, governance, report, integration, config |

## 跨域 HTTP 调用

跨域调用通过 RestTemplate HTTP 进行（运行时），编译期无循环依赖：

| 调用方 | 被调方 | 说明 |
|--------|--------|------|
| operation (workpermit) | realtime (location) | 区域人数查询 |
| operation (workpermit) | realtime (alarm) | 区域告警状态 |
| operation (workpermit) | risk (dualprevention) | 区域隐患检查 |
| realtime (alarm) | risk (dualprevention) | 告警转隐患 |
| realtime (location) | operation (contractor) | 人员准入校验 |
| risk (majorhazard) | realtime (alarm) | 重大危险源告警 |

## 内部调用（已消除 HTTP）

同域内原 RestTemplate 客户端已转为直接 Service 注入，共消除 7 个跨进程调用。
