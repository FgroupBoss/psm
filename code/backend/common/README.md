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
| `psm-common-core` | 通用工具、异常、基础 DTO（Lombok） | 无 |
| `psm-common-data` | MyBatis-Plus + Druid 配置 | core |
| `psm-common-web` | AbstractController、ResponseVO | core |
| `psm-common-feign` | Feign 拦截器/配置 | core |
| `psm-common-security` | 认证/鉴权基础 | core |

## Lombok 约定

- 父工程 `code/backend/pom.xml` 统一管理 `lombok` 版本与注解处理器；根目录 `lombok.config` 为全后端默认配置。
- **DTO / VO / Entity**：优先 `@Data`；需保留无参构造时加 `@NoArgsConstructor`；分页/响应等可用 `@AllArgsConstructor`。
- **Service / Controller**：构造注入用 `@RequiredArgsConstructor`；日志用 `@Slf4j`（勿手写 `LoggerFactory`）。
- **带 `@Value` 的配置字段**：仍使用显式构造器注入，勿与 `@RequiredArgsConstructor` 混用在同一类上。
- 各 `*-api` 模块已声明 `lombok`（`provided`）；新建类请与现有模块风格一致。
