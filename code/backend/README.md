# 后端微服务骨架

## 设计原则

- 服务按业务域拆分，不按页面拆分。
- 租户隔离、鉴权、审计、附件、通知作为平台能力统一沉淀。
- 工业数据只读接入，不做 DCS/SIS 反向控制。
- 每个服务默认包含 `api`、`application`、`domain`、`infrastructure`、`interfaces` 五类目录。
- 当前只放目录骨架，后续按项目技术栈补充 Maven、Spring Boot、配置和代码。

## 推荐后端技术栈

| 层级 | 建议 |
| --- | --- |
| 语言 | Java 8+，以最终项目约束为准 |
| 框架 | Spring Boot / Spring Cloud |
| 网关 | Spring Cloud Gateway 或企业统一网关 |
| 认证 | OAuth2/OIDC，兼容 SAML/CAS/LDAP/AD 适配 |
| 数据库 | MySQL/PostgreSQL/达梦/人大金仓，按现场要求适配 |
| 缓存 | Redis |
| 消息 | Kafka/RabbitMQ/RocketMQ |
| 文件 | MinIO/S3/企业对象存储 |
| 观测 | Prometheus、Grafana、ELK/Loki、OpenTelemetry |

## 当前脚手架

- Java 8 编译级别。
- Spring Boot 2.7.18。
- Maven 多模块（12 个子模块）。
- 默认 Web starter 排除 `spring-boot-starter-logging`，使用 `spring-boot-starter-log4j2`。
- `psm-common-core` 提供 `ResponseVO`、`BusinessException`、`CentralAuditClient`、`CentralNotificationClient`。

## 本地验证

```powershell
mvn compile -DskipTests
mvn -pl apis/psm-identity-api -am spring-boot:run
```

## 模块分层与打包顺序

```text
backend/
├── bom/                                 Layer 0: 版本管理中心
│   └── psm-common-bom/                  最先构建
│
├── common/                              Layer 1: 基础设施底座（5 个库）
│   ├── psm-common-core/                 根依赖
│   ├── psm-common-data/                 MyBatis-Plus 持久层
│   ├── psm-common-web/                  Web 层（Controller/响应）
│   ├── psm-common-feign/                微服务 Feign 调用
│   └── psm-common-security/             安全认证
│
└── apis/                                Layer 2: 领域服务（6 个 Spring Boot 应用）
    ├── psm-identity-api/                身份与接入（认证/授权/审计/文件/通知/主数据/网关）
    ├── psm-operation-api/               作业管控（许可/承包商/移动BFF）
    ├── psm-realtime-api/                实时感知（告警/定位/视频AI）
    ├── psm-risk-api/                    风险防控（危险源/巡检/双防）
    ├── psm-process-safety-api/          过程安全（PHA/MOC/PSSR/屏障/配置规则）
    └── psm-incident-governance-api/     事件治理（事故/CAPA/报表/监管上报）
```

> **打包顺序**：BOM → Common → 领域服务（identity → process-safety → incident-governance → risk → realtime → operation）。跨域调用走运行时 HTTP，编译期无循环依赖。

## 本地验证

```powershell
mvn -DskipTests=false test
mvn -pl services/psm-auth-service -am spring-boot:run
```
