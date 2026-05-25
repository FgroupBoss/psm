# 后端 AGENTS 规范与 MyBatis-Plus 整改跟踪

## 目标

将 `code/backend` 已有后端代码对齐仓库 AGENTS 约定，重点落实：

- Controller -> Service（接口 + impl）-> Mapper 分层。
- 请求对象放入 `model.dto`，响应对象放入 `model.vo`，持久化对象放入 `model.entity`。
- 数据访问严格使用 MyBatis-Plus，不再在业务代码中直接使用 `JdbcTemplate` / `repository`。
- SQL 映射文件放在 `src/main/resources/mapper/**/*.xml`。
- 保持 Java 8、Spring Boot 2.7.18、Log4j2、构造注入、入参校验等既有约束。

## 当前主要偏差

| 序号 | 类型 | 当前情况 | 目标状态 | 影响模块 | 状态 |
| --- | --- | --- | --- | --- | --- |
| 1 | 数据访问 | 使用 `spring-boot-starter-jdbc`、`JdbcTemplate`、`repository` | 使用 MyBatis-Plus `Mapper` + XML | `psm-auth-service`、`psm-master-data-service`、`psm-audit-service` | 待整改 |
| 2 | 分层命名 | Controller 位于 `interfaces` 包 | Controller 位于 `controller` 包 | auth、master-data、audit | 待整改 |
| 3 | Service 结构 | Service 是具体类 | `service` 接口 + `service.impl` 实现 | auth、master-data、audit、gateway | 待整改 |
| 4 | 模型分包 | DTO、VO、Entity 混放在 `model` | 拆为 `model.dto`、`model.vo`、`model.entity` | auth、master-data、audit | 待整改 |
| 5 | 响应脱敏 | `AuthUser` 同时承载持久化和响应 | `AuthUserEntity` 与 `AuthUserVO` 分离，响应不含敏感字段 | `psm-auth-service` | 已整改 |
| 6 | Mapper XML | 无 `resources/mapper` 映射目录 | 新增 `src/main/resources/mapper/**/*.xml` | auth、master-data、audit | 待整改 |
| 7 | Mapper 扫描 | 应用入口未配置 `@MapperScan` | DB 服务入口增加对应 mapper 扫描 | auth、master-data、audit | 待整改 |
| 8 | 入参校验 | `@RequestBody` 缺少 `@Valid`，DTO 缺少校验注解 | DTO 使用 `javax.validation`，Controller 使用 `@Valid` | auth、master-data | 待整改 |
| 9 | 日志风格 | 部分异常处理类手写 `LoggerFactory` | 使用 Lombok `@Slf4j` | auth、master-data、gateway | 待整改 |
| 10 | 构造注入 | 多处手写构造器 | 使用 `@RequiredArgsConstructor` + `final` 字段 | auth、master-data、audit、gateway | 待整改 |
| 11 | SQL 注释编码 | 部分 `schema.sql` 中文注释显示乱码 | 确认来源并统一 UTF-8 | auth、master-data、audit | 待确认 |

## 改造范围

### 第一批：已有数据库访问模块

- `code/backend/services/psm-auth-service`
- `code/backend/services/psm-master-data-service`
- `code/backend/services/psm-audit-service`

### 第二批：非 DB 或轻量模块

- `code/backend/services/psm-gateway`

Gateway 没有数据库访问，主要整改 Service 接口拆分、包名、日志和构造注入。

### 暂不改动

只有启动类、配置文件、README 或占位资源的服务模块，除非后续新增业务代码，否则本轮不做结构性重构。

## 服务改造进度台账

> 状态说明：`未开始`、`进行中`、`待验证`、`已完成`、`阻塞`。
> 每完成一次模块改造或验证，需要更新对应服务的状态、完成项、遗留项和最后更新时间。

| 服务 | 范围 | 当前状态 | 当前阶段 | 完成项 | 遗留项 / 下一步 | 最后更新 |
| --- | --- | --- | --- | --- | --- | --- |
| `psm-auth-service` | DB + Controller + Service + Mapper + DTO/VO/Entity | 已完成 | 阶段 2 | 引入 MyBatis-Plus；迁移 `controller/service.impl/mapper/model.*`；删除 `AuthRepository`；DTO 校验与 VO 脱敏完成；接口/实现注释补齐；模块及全量测试通过 | 无 | 2026-05-25 |
| `psm-master-data-service` | DB + Controller + Service + Mapper + DTO/VO/Entity | 未开始 | 阶段 3 | 无 | 拆 `MasterDataItem` 与基础数据多表 Mapper，替换动态 Repository SQL | 2026-05-25 |
| `psm-audit-service` | DB + Controller + Service + Mapper + VO/Entity | 未开始 | 阶段 4 | 无 | 替换 `AuditLogRepository`，迁移动态查询到 Mapper XML | 2026-05-25 |
| `psm-gateway` | Controller + Service + 日志风格 | 已完成 | 阶段 5 | Controller 迁入 `controller`；`GatewayAuthService` 拆为接口与 `service.impl` 实现；日志与构造注入已调整；单模块测试通过 | 无 | 2026-05-25 |
| 其他后端服务 | 启动类 / 配置 / 占位模块 | 暂不改动 | 暂不纳入 | 已确认本轮不做结构性重构 | 后续新增业务代码时按本规范落地 | 2026-05-25 |

### psm-auth-service 进度明细

- [x] POM 引入 MyBatis-Plus 与 validation。
- [x] 启动类增加 `@MapperScan`。
- [x] `interfaces` 迁移为 `controller`。
- [x] `AuthService` 拆为接口与 `service.impl.AuthServiceImpl`。
- [x] 删除 `AuthRepository`，新增 MyBatis-Plus Mapper 与 XML。
- [x] `model` 拆分为 `model.dto`、`model.vo`、`model.entity`。
- [x] `AuthUser` 响应脱敏，避免暴露 `passwordHash`。
- [x] `IdentityProvider` 响应脱敏，避免暴露 `clientSecret`。
- [x] Controller 入参增加 `@Valid`，DTO 增加 `javax.validation` 注解。
- [x] 全局异常处理补充参数校验异常。
- [x] 日志统一为 `@Slf4j`。
- [x] 构造注入统一为 `@RequiredArgsConstructor`。
- [x] 单模块测试通过。

### psm-master-data-service 进度明细

- [ ] POM 引入 MyBatis-Plus 与 validation。
- [ ] 启动类增加 `@MapperScan`。
- [ ] `interfaces` 迁移为 `controller`。
- [ ] `MasterDataService` 拆为接口与 `service.impl.MasterDataServiceImpl`。
- [ ] `BaseDataService` 拆为接口与 `service.impl.BaseDataServiceImpl`。
- [ ] 删除 `MasterDataRepository`，新增 `MasterDataItemMapper` 与 XML。
- [ ] 删除 `BaseDataRepository`，新增基础数据多表 Mapper 与 XML。
- [ ] 新增 `BaseAreaEntity`、`BaseUnitEntity`、`BaseEquipmentEntity`、`MonitorPointEntity`。
- [ ] `model` 拆分为 `model.dto`、`model.vo`、`model.entity`。
- [ ] `attributes json` 字段确认 TypeHandler 或 Service 层转换方案。
- [ ] Controller 入参增加 `@Valid`，DTO 增加 `javax.validation` 注解。
- [ ] 全局异常处理补充参数校验异常。
- [ ] 日志统一为 `@Slf4j`。
- [ ] 构造注入统一为 `@RequiredArgsConstructor`。
- [x] 单模块测试通过。

### psm-audit-service 进度明细

- [ ] POM 引入 MyBatis-Plus。
- [ ] 启动类增加 `@MapperScan`。
- [ ] `interfaces` 迁移为 `controller`。
- [ ] `AuditLogService` 拆为接口与 `service.impl.AuditLogServiceImpl`。
- [ ] 删除 `AuditLogRepository`，新增 `AuditChangeLogMapper` 与 XML。
- [ ] `AuditLogRecord` 拆分为 `AuditChangeLogEntity` 与 `AuditLogRecordVO`。
- [ ] 动态分页查询迁移到 Mapper XML。
- [ ] 日志统一为 `@Slf4j`。
- [ ] 构造注入统一为 `@RequiredArgsConstructor`。
- [ ] 单模块测试通过。

### psm-gateway 进度明细

- [x] `GatewayProxyController` 迁移到 `controller` 包。
- [x] `GatewayExceptionHandler` 迁移到 `controller` 包。
- [x] `GatewayAuthService` 拆为接口与 `service.impl.GatewayAuthServiceImpl`。
- [x] 日志统一为 `@Slf4j`。
- [x] 构造注入统一为 `@RequiredArgsConstructor`。
- [x] 保持代理异常状态码透传逻辑。
- [ ] 单模块测试通过。

## 统一技术方案

### Maven 依赖

DB 服务模块：

- 移除业务直接依赖的 `spring-boot-starter-jdbc`。
- 新增 MyBatis-Plus starter。
- 新增或确认 validation 依赖。
- 保留 `spring-boot-starter-log4j2` 与 `spring-boot-starter-logging` exclusions。

建议优先在父 `pom.xml` 的 `dependencyManagement` 中统一 MyBatis-Plus 版本，再由子模块按需引入。

### 应用入口

DB 服务启动类增加 Mapper 扫描，例如：

```java
@MapperScan("com.fgroupboss.ai.psm.auth.mapper")
```

多模块服务按各自包路径配置，不建议扫描过宽。

### 分层目录

目标目录示例：

```text
src/main/java/com/fgroupboss/ai/psm/auth
  controller
  service
  service/impl
  mapper
  model/dto
  model/vo
  model/entity
```

Mapper XML：

```text
src/main/resources/mapper/AuthUserMapper.xml
src/main/resources/mapper/AuthSessionMapper.xml
```

## 模块级整改清单

### psm-auth-service

#### 包与类迁移

- `interfaces/AuthController` -> `controller/AuthController`
- `interfaces/DemoController` -> `controller/DemoController`
- `interfaces/GlobalExceptionHandler` -> `controller/GlobalExceptionHandler`
- `service/AuthService` -> `service/AuthService` 接口
- 新增 `service/impl/AuthServiceImpl`
- 删除 `repository/AuthRepository`
- 新增 Mapper：
  - `AuthUserMapper`
  - `AuthSessionMapper`
  - `IdentityProviderMapper`
  - `AuthUserIdentityMapper`
  - `AuthSsoStateMapper`
  - `LoginLogMapper`

#### 模型拆分

- DTO：
  - `RegisterRequest`
  - `LoginRequest`
  - `RefreshTokenRequest`
  - `SsoCallbackRequest`
- VO：
  - `AuthTokenResponse`
  - `AuthUserVO`
  - `IdentityProviderVO`
  - `SsoLoginResponse`
- Entity：
  - `AuthUserEntity`
  - `AuthSessionEntity`
  - `IdentityProviderEntity`
  - `AuthUserIdentityEntity`
  - `AuthSsoStateEntity`
  - `LoginLogEntity`

#### 重点注意

- 响应对象不得包含 `passwordHash`、`clientSecret` 等敏感字段。
- `config json` 字段需要明确 MyBatis TypeHandler 方案：
  - 简单方案：Entity 中使用 `String config`，Service 转 `Map`。
  - 标准方案：自定义 JSON TypeHandler。
- 现有登录失败、SSO state 消费、会话吊销逻辑要保持事务边界。

### psm-master-data-service

#### 包与类迁移

- `interfaces/*Controller` -> `controller`
- `interfaces/GlobalExceptionHandler` -> `controller/GlobalExceptionHandler`
- `service/MasterDataService` -> 接口
- `service/BaseDataService` -> 接口
- 新增：
  - `service/impl/MasterDataServiceImpl`
  - `service/impl/BaseDataServiceImpl`
- 删除：
  - `repository/MasterDataRepository`
  - `repository/BaseDataRepository`

#### Mapper

- `MasterDataItemMapper`
- `AuditChangeLogMapper`
- `BaseAreaMapper`
- `BaseUnitMapper`
- `BaseEquipmentMapper`
- `MonitorPointMapper`

#### 模型拆分

- DTO：
  - `MasterDataRequest`
  - `BaseDataRequest`
- VO：
  - `MasterDataRecordVO`
  - `BaseDataRecordVO`
- Entity：
  - `MasterDataItemEntity`
  - `AuditChangeLogEntity`
  - `BaseAreaEntity`
  - `BaseUnitEntity`
  - `BaseEquipmentEntity`
  - `MonitorPointEntity`

#### 重点注意

- `BaseDataRepository` 当前使用 `BaseDataType` 动态拼表名和字段。严格 MyBatis-Plus 下不建议继续拼接动态 SQL。
- 建议按表拆 Mapper 与 Entity，Service 层保留 `BaseDataType` 作为业务路由。
- `attributes json` 字段同样需要 TypeHandler 或 Service 层转换方案。
- 审计写入 `audit_change_log` 可独立封装为内部私有方法或独立 Mapper 调用。

### psm-audit-service

#### 包与类迁移

- `interfaces/AuditLogController` -> `controller/AuditLogController`
- `interfaces/GlobalExceptionHandler` -> `controller/GlobalExceptionHandler`
- `service/AuditLogService` -> 接口
- 新增 `service/impl/AuditLogServiceImpl`
- 删除 `repository/AuditLogRepository`
- 新增 `AuditChangeLogMapper`

#### 模型拆分

- DTO：
  - 如后续存在复杂查询，可新增 `AuditLogQueryDTO`
- VO：
  - `AuditLogRecordVO`
- Entity：
  - `AuditChangeLogEntity`

#### 重点注意

- 当前分页查询是动态条件查询，建议用 XML 承接，避免 Service 拼 SQL。
- `pageNo/pageSize` 的归一化逻辑保持不变。

### psm-gateway

#### 包与类迁移

- `GatewayProxyController` 建议迁入 `controller` 包。
- `GatewayExceptionHandler` 建议迁入 `controller` 包。
- `GatewayAuthService` 拆为：
  - `service/GatewayAuthService`
  - `service/impl/GatewayAuthServiceImpl`

#### 重点注意

- Gateway 无 MyBatis-Plus 改造项。
- 保持代理异常状态码透传逻辑。
- 日志改为 `@Slf4j`。

## 通用编码整改

### 构造注入

统一使用：

```java
@RequiredArgsConstructor
public class XxxServiceImpl implements XxxService {
    private final XxxMapper xxxMapper;
}
```

### 日志

统一使用：

```java
@Slf4j
```

禁止：

- `LoggerFactory.getLogger(...)`
- `System.out`
- `printStackTrace`
- 日志字符串拼接

### 入参校验

Controller：

```java
public ResponseVO<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request)
```

DTO：

```java
@NotNull
private Long tenantId;

@NotBlank
private String username;
```

全局异常处理补充 `MethodArgumentNotValidException`。

### 异常

业务错误继续使用 `BusinessException`，不要在 Controller 中吞异常。

### 敏感字段

禁止向 VO 暴露：

- 密码、密码哈希
- token 原始值之外的内部密钥
- SSO `clientSecret`
- 其他认证密钥

## 建议实施顺序

### 阶段 1：基础设施

- [x] 父 POM 增加 MyBatis-Plus 版本管理。
- [ ] DB 子模块引入 MyBatis-Plus starter。
- [ ] DB 子模块移除直接 `spring-boot-starter-jdbc`。
- [ ] 增加 validation 依赖。
- [ ] DB 服务启动类增加 `@MapperScan`。
- [ ] 配置或确认 mapper XML 扫描路径。

### 阶段 2：psm-auth-service

- [x] 拆分 `controller/service/service.impl/mapper/model.dto/model.vo/model.entity`。
- [x] 用 Mapper + XML 替换 `AuthRepository`。
- [x] 拆分 `AuthService` 接口与实现。
- [x] 补 DTO 校验。
- [x] 补 VO 脱敏。
- [x] 更新测试。
- [x] 单模块测试通过。

### 阶段 3：psm-master-data-service

- [ ] 拆分包结构。
- [ ] 拆 `MasterDataItem` 相关 Entity/Mapper/XML。
- [ ] 拆 `BaseArea/BaseUnit/BaseEquipment/MonitorPoint` Entity/Mapper/XML。
- [ ] 用 Service 路由替代 Repository 动态拼表。
- [ ] 补 DTO 校验。
- [ ] 更新测试。
- [ ] 单模块测试通过。

### 阶段 4：psm-audit-service

- [ ] 拆分包结构。
- [ ] 用 Mapper + XML 替换 `AuditLogRepository`。
- [ ] 拆分 Service 接口与实现。
- [ ] 更新测试。
- [ ] 单模块测试通过。

### 阶段 5：psm-gateway

- [x] 调整包结构。
- [x] 拆分 Service 接口与实现。
- [x] 日志改为 `@Slf4j`。
- [x] 更新测试。
- [ ] 单模块测试通过。

### 阶段 6：全量验证

- [x] 执行 `mvn -DskipTests=false test`。
- [ ] 修复编译与测试失败。
- [ ] 复查 `rg "JdbcTemplate|repository|LoggerFactory|printStackTrace|System\\.out"`。
- [ ] 复查 `src/main/resources/mapper/**/*.xml` 是否齐全。
- [ ] 复查 Controller 入参是否使用 `@Valid`。

## 风险与取舍

- MyBatis-Plus 改造会影响大量 import、Bean 名称、测试和事务路径，建议按模块分批提交。
- `BaseDataRepository` 的动态表逻辑是最大风险点，拆表后需要重点回归新增、更新、树查询、分页和引用校验。
- DTO/VO/Entity 拆分会改变 Service 返回类型和 Controller 响应组装，需要明确前端契约是否允许字段名保持不变。
- Validation 上移到 Controller 后，部分错误信息可能从业务异常变为参数校验异常，需要统一返回格式。
- SQL 文件当前有中文乱码，若确认是编码问题，应单独修复，避免和 MyBatis-Plus 重构混在一个提交。

## 完成标准

- 不再存在业务代码直接使用 `JdbcTemplate`。
- 不再存在 `repository` 包作为数据访问层。
- DB 服务均存在 `mapper` 包和 `resources/mapper/**/*.xml`。
- Service 均为接口 + `impl` 实现。
- 请求、响应、实体对象分别位于 `model.dto`、`model.vo`、`model.entity`。
- Controller 使用 `@Valid` 触发参数校验。
- 异常处理、日志、构造注入符合 AGENTS 规则。
- `mvn -DskipTests=false test` 通过。
