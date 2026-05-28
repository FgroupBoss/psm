# 部署配置骨架

本目录用于放置部署、环境、网关、观测和中间件配置。

当前只保留目录骨架：

- `env`：环境变量模板。
- `gateway`：网关路由配置。
- `k8s`：Kubernetes 部署清单。
- `docker`：Dockerfile 和 compose 模板。
- `observability`：日志、指标、链路追踪配置。

## 二期服务清单（已完成签收）

| 服务 | 端口 | 说明 |
| --- | --- | --- |
| `psm-dual-prevention-service` | 18101 | 双重预防与隐患 |
| `psm-inspection-service` | 18102 | 智能巡检 |
| `psm-location-service` | 18103 | 人员定位与封闭化 |
| `psm-video-service` | 18104 | 视频 AI 与过程监护 |
| `psm-integration-service` | 18091 | 监管园区接口（扩展） |
| `psm-work-permit-service` | 18088 | SIMOPS 扩展（同实例） |

部署时需为上述服务配置独立数据库（见各服务 `src/main/resources/db/schema.sql`）及 Nacos/环境变量中的下游服务 URL（audit、notification、alarm、dual-prevention 等）。
