# 后端共享库

共享库用于放置跨服务复用能力，后续实现时可按 Maven module 或内部依赖包拆分。

建议拆分：

- `psm-common-core`：通用返回、异常、基础常量。
- `psm-common-security`：Token、权限上下文、租户上下文。
- `psm-common-web`：Web 拦截器、统一响应、参数校验。
- `psm-common-audit`：审计注解、审计事件模型。
- `psm-common-tenant`：租户隔离、数据权限过滤。
- `psm-common-mq`：消息事件模型和生产消费封装。

