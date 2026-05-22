# psm-auth-service

认证服务。

职责：

- 本地账号登录、退出、Token 刷新。
- 管理员邀请注册。
- 通用 SSO 适配：OIDC/OAuth2 优先，SAML/CAS/LDAP/AD 预留。
- 租户身份源配置读取。
- 外部身份与本地用户映射。
- 登录日志和认证事件。

