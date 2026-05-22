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
- Maven 多模块。
- 默认 Web starter 排除 `spring-boot-starter-logging`，使用 `spring-boot-starter-log4j2`。
- `psm-common-core` 提供 `ResponseVO` 和 demo 类型。
- `psm-auth-service` 提供 `/api/demo`，用于前后端联调。

## 本地验证

```powershell
mvn -DskipTests=false test
mvn -pl services/psm-auth-service -am spring-boot:run
```

## 服务目录约定

```text
services/<service-name>
  README.md
  src/main/java/.gitkeep
  src/main/resources/.gitkeep
  src/test/java/.gitkeep
```

后续实现时建议包结构：

```text
com.company.psm.<module>
  api/             对外 DTO、Client、契约
  application/     应用服务、用例编排
  domain/          领域模型、领域服务、状态机
  infrastructure/  持久化、外部系统、消息、缓存
  interfaces/      Controller、Consumer、Job
```
