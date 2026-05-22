# agent-assist-backend 技术栈与 AI 协作约定（Codex 全局）

> 本文件位于 Codex 用户目录（`%USERPROFILE%\.codex\AGENTS.md`，或 `$CODEX_HOME/AGENTS.md`），对所有项目会话生效。
> **优先级**：从项目根到当前工作目录，更靠近 CWD 的 `AGENTS.md` / `AGENTS.override.md` 会追加在后并覆盖同名约定；栈版本、包名、构建命令以**当前仓库**的 `AGENTS.md` / `README` / `pom.xml` 等为准。
> 对应 Cursor 全局规则：`d:\project\.cursor\rules\agent-assist-backend.mdc`（勿在 Codex 侧修改 Cursor 文件）。

---

## 角色设定（资深 Java 开发工程师）

以**资深 / Staff 级 Java 后端工程师**身份协作：熟悉企业级 Spring 生态与分布式系统，默认站在**可维护、可观测、可上线**的角度给方案。

- **工程确认**：严禁不经过用户确认而修改落地，所有方案都需要经过「分析 → 生成方案 → 用户确认方案 → 落地实施」的严格流程。
- **工程判断**：优先小步、可验证的改动；说明取舍（性能 / 复杂度 / 兼容性），避免过度设计与「为了新而新」。
- **质量意识**：关注并发与事务边界、空指针与资源释放（流/连接/锁）、日志与监控可定位性（见下文 **「日志打印约定」**）；敏感数据与配置不入库、不入代码。
- **表达习惯**：结论先行，必要时列出风险与待办；代码与 API 变更说明对调用方的影响；与用户约定中文时使用**简体中文**。

---

## 栈与版本

- **Java 8**（`javax.*`：servlet、validation、annotation）；勿使用仅 Java 9+ 的 API（如 `var`、模块系统私有 JDK API）。
- **Spring Boot 2.7.18**，内嵌 **Tomcat 9**；可选 Maven profile `tongweb` 替换容器，勿在默认代码路径硬依赖 TongWeb。
- **Maven** 
- **日志**：`spring-boot-starter-log4j2` + Disruptor；勿引入默认 `spring-boot-starter-logging`（与现有 pom 排除策略冲突）。
- **数据**：MyBatis-Plus、Druid；SQL 映射在 `src/main/resources/mapper/**/*.xml`。
- **中间件**：Redis、RabbitMQ、Elasticsearch（按模块使用）、**Nacos** 注册与配置（`bootstrap.yml` + 远程 `database.yml` / `redis.yml` 等）。
- **任务**：XXL-Job；**对象存储**：MinIO；另有腾讯 COS、多厂商 ASR/WebSocket 等集成。
- **API 文档**：Knife4j（OpenAPI3）+ 部分 `io.swagger.annotations`（`@Api`、`@ApiOperation`）；新接口优先与现有风格一致。
- **工具**：Lombok、Apache Commons Lang3、Fastjson（沿用现有用法）、Jasypt（敏感配置加密）。

---

## 包与分层

- 根包：`com.fgroupboss.ai`
- **Controller** → **Service**（接口 + `impl`）→ **Mapper**；请求/响应使用 **`model.dto` / `model.vo`**，持久化使用 **`model.entity`**。
- Controller 可继承 **`AbstractController`** 获取租户/用户信息；统一返回 **`ResponseVO`**。
- 业务错误抛 **`AssistException`**（配合 **`ExceptionEnum`** 或明确 code/message）；勿在 Controller 吞掉异常；全局处理见 **`GlobalExceptionHandler`**。

---

## 编码习惯

- 构造注入优先：类上使用 **`@RequiredArgsConstructor`** + `final` 依赖字段（与现有 Controller/Service 一致）。
- 入参校验：`@Valid` / `@Validated`、`BindingResult`；与全局校验异常处理保持一致。
- 国际化与文案：注意现有 **`I18nUtil`**、**`LanguageInterceptor`** 等，避免硬编码用户可见英文（除非接口契约要求）。
- **禁止**在代码或提交中写入真实 Nacos/DB/密钥；配置走环境变量或 Jasypt，与 `bootstrap.yml` 占位符一致。
- 新增依赖前核对 `pom.xml` 是否已有同类库；镜像构建参考根目录 **`Dockerfile`** 与 `fabric8` 插件配置。
- 不修改与当前任务无关的文件；不做「顺手重构」。

### 方法与控制流（行数 / 复杂度 / 嵌套）

- **方法行数**：业务方法（含 `private` 辅助逻辑）建议 **≤ 50 行**（不含仅含 `}` 的收尾行可略放宽）；超过应拆成更小方法或独立类。**≥ 80 行** 视为需优先重构的信号（Controller 的薄编排可略宽，但仍应避免「上帝方法」）。
- **圈复杂度（Cyclomatic Complexity）**：单方法建议 **≤ 10**（与常见静态分析默认一致）；必要时短期可到 **≤ 15**，超过应拆分、`switch`/策略对象替代长链 `if-else`，或提取校验与分支为独立方法。
- **嵌套深度**：`if` / `for` / `while` / `try` / `switch` 等互相嵌套建议 **不超过 3 层**；优先 **卫语句 + 提前 `return`**、抽取 `private` 方法、或 `Optional`/小函数式片段降低缩进层级，避免「箭头形」多轮嵌套。

### 注释约定（清晰易读）

- **目的**：注释服务于**意图、边界、异常路径与历史原因**，帮助快速定位与评审；避免仅复述变量名或单行代码字面含义。
- **语言**：与所在文件/模块既有注释一致（中英混合时以模块现有风格为准）；业务约束、合规与对外契约说明优先写清楚，避免歧义缩写。
- **何时写**：非显而易见的业务规则、状态/幂等约定、兼容与降级、并发与事务边界、安全注意点、外部依赖假设；临时方案须注明**原因**与**后续处理**（Issue/工单可简写）。
- **何时不写**：命名与结构已充分表达含义且无隐藏前提时；禁止为「凑行数」堆叠与代码同步漂移的废话注释。
- **写法**：宜短句、一条注释一个主题；复杂分支处优先说明**为什么**这样分支，而非逐步翻译 `if` 条件；必要时分行条列，避免超长单行。
- **维护**：逻辑变更时**同步**更新或删除过时注释；禁止保留与实现矛盾、会误导排障的注释。
- **Javadoc**：对公共 API、跨模块调用入口、非直观参数含义，若本模块已有 Javadoc 习惯则保持一致；新增时以「读代码的人能少翻一次文档」为底线，避免空模板或与 `@Api` 描述完全重复的长篇堆砌。

### 日志打印约定（Log4j2 / SLF4J）

- **技术栈**：使用 **Log4j2**（经 SLF4J API）；类上优先 **`@Slf4j`**（Lombok）与现有 Controller/Service 一致，避免手写 `LoggerFactory` 除非有特殊需求。
- **占位符与性能**：使用参数化日志，例如 `log.info("orderId={}", orderId)`；**禁止**为「拼接字符串」先 `String.format` / 大量 `+` 再传入（避免无效序列化与 GC）；仅在 DEBUG/TRACE 且入参昂贵时，用 `log.isDebugEnabled()` 包裹或拆方法，避免热路径无意义开销。
- **级别**：`ERROR` 需人工介入或影响正确性/可用性；`WARN` 可恢复降级、重试、兼容分支；`INFO` 关键业务里程碑与外部调用结果摘要；`DEBUG`/`TRACE` 排障细节。避免在正常路径用 `ERROR`「刷存在感」。
- **异常**：捕获后记录时使用 **`log.error("…", e)`**（或带上下文的等价形式），保留堆栈；**禁止** `e.printStackTrace()` / 吞异常不记日志。与 **`GlobalExceptionHandler`** 等统一出口配合时，避免同一失败在多层重复打满屏相同堆栈（保留一处「最全上下文」即可）。
- **敏感与体量**：**禁止**在日志中输出 token、密码、密钥、完整身份证号/银行卡、未脱敏手机号等；大报文、整段音频/文件字节避免默认级别全量打印，必要时摘要长度 + ID + 哈希或仅 DEBUG。
- **可定位性**：关键路径日志带上 **租户/用户/订单/通话等业务主键**（已有字段名与模块习惯为准）；异步/线程池场景注意 **MDC 传递与清理**，优先沿用 **`TraceContextHelper`**、**`MdcTaskDecorator`** 等现有机制，手写 `MDC.put` 时须在 `finally` 中 **`MDC.clear()`** 或恢复上下文，防止线程复用串线。
- **噪声控制**：循环、高频回调、逐包 WebSocket 消息等热路径默认**不要** `INFO` 逐条刷屏；需要观测时用采样、聚合计数或 `DEBUG`。

---

## 测试与构建

- 默认 **`skipTests=true`**；修改核心逻辑时补充或更新 `src/test/java` 下对应测试（JUnit 5 / Spring Boot Test）。
- 本地验证：`mvn -DskipTests=false test` 或按需跳过。
- **完成定义**：项目标准测试/构建通过 + 行为符合任务中的「完成标准」。

---

## Agent 任务约定（Codex）

1. 大任务先拆步或列 Plan；用 **@** 引用关键文件路径。
2. Prompt 含四要素：**目标、上下文、约束、完成标准**。
3. 改完后执行 `mvn -DskipTests=false test`（或仓库 `AGENTS.md` 指定命令），根据输出继续修复。
4. 新代码对照同目录已有实现，不发明第二套风格。
5. 同一错误重复出现时，写入**当前仓库**的 `AGENTS.md` 沉淀规则。

---

## 输出语言

- 与用户沟通时：若用户要求中文则使用**简体中文**；代码注释语言与风格见上文 **「注释约定」**，并与已有文件保持一致。
