# PSM 平台代码框架

该目录与 `design` 平级，用于承载企业级石油化工 PSM 安全管理平台的一期前后端分离微服务工程骨架。

当前只搭建框架，不实现业务功能。模块划分基于 `design/10_一期落地设计`：

- 基础主数据与权限审计
- 系统配置与规则引擎
- 承包商准入
- 重大危险源
- 报警中心
- 危险工作票
- 移动端现场作业
- 报表大屏与上线验收

## 目录结构

```text
code
  backend/          后端微服务骨架
  frontend/         前端应用骨架
  deploy/           部署、网关、观测和环境配置占位
  scripts/          按需生成服务和前端模块目录的脚本
  docs/             代码侧工程约定
```

## 后端边界

后端按业务域拆分微服务，服务间通过 API、消息和事件解耦。每个服务只保留目录和职责说明，后续实现时再补充 `pom.xml`、源码、配置和测试。

| 服务 | 对应设计模块 |
| --- | --- |
| `psm-gateway` | 统一网关、鉴权入口、路由 |
| `psm-auth-service` | 注册登录、SSO、Token、租户身份源 |
| `psm-iam-service` | 多租户、用户、组织、角色、页面权限、数据权限 |
| `psm-master-data-service` | 区域、装置、设备、点位 |
| `psm-config-rule-service` | 字典、表单、流程、规则、通知配置 |
| `psm-contractor-service` | 承包商准入、人员、证书、培训、黑名单 |
| `psm-major-hazard-service` | 重大危险源、一源一档、点位绑定 |
| `psm-alarm-service` | 报警接入、分级、去重、处置闭环 |
| `psm-work-permit-service` | 危险工作票、审批、许可、监护、验收 |
| `psm-mobile-bff` | 移动端现场作业 BFF |
| `psm-report-service` | 报表、大屏指标、导出 |
| `psm-integration-service` | HR/OA/GDS/DCS/门禁等集成适配 |
| `psm-file-service` | 附件、签名、归档文件 |
| `psm-notification-service` | 站内信、短信、邮件、企业微信/钉钉预留 |
| `psm-audit-service` | 操作日志、审计追溯、登录日志 |

## 前端边界

前端按应用拆分：

- `admin-web`：PC 管理端。
- `mobile-site`：移动端现场作业 H5/企业移动门户适配。
- `dashboard-web`：中控大屏。

共享能力放在 `frontend/packages`，包括 API SDK、权限工具、UI 组件和业务类型定义。

## 按需生成

后续新增服务或前端模块时优先使用：

- `scripts/New-BackendService.ps1`
- `scripts/New-FrontendApp.ps1`
- `scripts/New-FrontendPackage.ps1`

