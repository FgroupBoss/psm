# Common 基础设施

**Layer 1** — 所有模块的通用底座，按依赖关系排列。

```
psm-common-core        ← 根依赖（所有模块均依赖它）
  ├── psm-common-data      ← MyBatis-Plus 持久层基础
  ├── psm-common-web       ← Web 层基础（Controller / 响应封装）
  ├── psm-common-feign     ← 微服务间 Feign 调用基础
  └── psm-common-security  ← 安全认证基础
```

| 模块 | 说明 | 传递依赖 |
|------|------|----------|
| `psm-common-core` | 通用工具、异常、基础 DTO | 无 |
| `psm-common-data` | MyBatis-Plus + Druid 配置 | core |
| `psm-common-web` | AbstractController、ResponseVO | core |
| `psm-common-feign` | Feign 拦截器/配置 | core |
| `psm-common-security` | 认证/鉴权基础 | core |
