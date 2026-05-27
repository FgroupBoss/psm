# psm-notification-service

消息中心（一期站内信）。

## 能力

- 站内信发送、收件箱、已读标记（`IN_APP`）
- 短信/邮件/企业微信/钉钉通道枚举预留，一期不实际外发
- 内置模板：`ALARM_ESCALATION`、`CERT_EXPIRE_WARN`、`WKP_TIMEOUT_WARN`

## 端口

- 默认 `18093`
- 数据库 `psm_notification`，执行 `src/main/resources/db/schema.sql`

## API

- `POST /api/notifications/send` — 业务/内部发送（`requestId` 幂等）
- `GET /api/notifications/inbox` — 收件箱分页
- `POST /api/notifications/{id}/read` — 标记已读

网关前缀：`/api/notifications/**`
