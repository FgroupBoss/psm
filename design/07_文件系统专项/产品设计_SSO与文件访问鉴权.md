# 产品设计 · SSO 与文件访问鉴权

## 1. 目标

无论用户通过**本地账号**还是 **SSO（OIDC/OAuth2/SAML 等）** 登录，访问文件的路径与权限模型一致：**先平台身份，再业务权限，最后才读字节**。

> SSO 在本专项中指「统一身份登录态」，不是「用企业网盘 SSO 代替平台存储」。

## 2. 身份与文件 API 的关系

```text
用户 → SSO/本地登录 → 平台 Access Token（含 tenantId、userId）
    → 上传/下载 API 携带 Authorization + X-PSM-* 头
    → 网关鉴权 → 文件服务校验租户
    → （可选）校验用户对 bizType/bizId 的操作权
    → StorageAdapter 读对象
```

| 步骤 | 失败处理 |
| --- | --- |
| Token 无效/过期 | 401，移动端引导重新登录（可再走 SSO） |
| 租户不匹配 | 403 |
| 无业务数据权限 | 403 |
| 文件不存在或已删 | 404 |

## 3. SSO 场景要点

| 场景 | 设计 |
| --- | --- |
| 首次 SSO 登录 | 映射本地用户后，上传 `createdBy` 记平台用户名 |
| SSO-only 用户 | 无本地密码，文件权限仍走 RBAC |
| 退出登录 | 平台 Token 失效；已发出的预签名 URL 仍按自身 TTL 过期 |
| 多租户 SSO | `state` 绑定 tenantId；文件请求必须带同一 tenant |

SSO 流程本身见 `01_基础主数据与权限审计/落地设计.md` §6；本模块**不实现**协议，只消费鉴权结果。

## 4. 下载授权模式

| 模式 | 编码 | 说明 |
| --- | --- | --- |
| 平台代理 | `PROXY` | 服务读对象后流式响应（LOCAL、内网 MinIO） |
| 预签名重定向 | `PRESIGNED` | 校验通过后 302 到云厂商短时 URL |
| 内部服务 | `SERVICE` | Feign 带内部 Token，不走浏览器 |

默认策略：

- `LOCAL`、`MINIO`（内网）：`PROXY`
- `ALIYUN_OSS`、`TENCENT_COS`、`HUAWEI_OBS` 等：`PRESIGNED`

配置项：`psm.file.download-mode` 或租户 profile 级覆盖。

## 5. 预签名安全

| 项 | 要求 |
| --- | --- |
| TTL | 默认 300s，最大 900s |
| 单次 | 同一用户同一 fileId 可限流 |
| Content-Disposition | 带上原始文件名（UTF-8 编码） |
| 审计 | 记录 `FILE_DOWNLOAD`、userId、fileId、模式 PRESIGNED |

预签名 URL **不得**缓存到前端 localStorage 长期复用。

## 6. 与网关、移动端

| 通道 | 说明 |
| --- | --- |
| Web | `Authorization: Bearer`；下载可用 window.open 带 Token（或 cookie 会话二期） |
| 移动 | 与现网一致：`/api/mobile/files/upload` 代理到 file 服务并透传 `X-PSM-User-Id` |
| 外链分享 | 一期不做；二期可做「需登录的分享码」 |

## 7. 审计事件

| 事件 | 记录 |
| --- | --- |
| `FILE_UPLOAD` | tenant、user、fileId、bizType、size、backend |
| `FILE_DOWNLOAD` | 同上 + downloadMode |
| `FILE_DELETE` | 操作人、原因 |
| `STORAGE_TEST` | 配置档测试结果 |

## 8. 验收标准

- AC-SSO-01：SSO 登录用户上传的文件，`createdBy` 为映射后的平台账号。
- AC-SSO-02：Token 过期后 download 返回 401。
- AC-SSO-03：COS 预签名链接过期后不可再访问；平台侧 audit 有 DOWNLOAD 记录。
