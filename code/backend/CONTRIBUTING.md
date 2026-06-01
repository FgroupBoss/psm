# 微服务治理规则（2026-05-31 冻结）

> 依据：`design/04_四期微服务治理/执行计划_微服务治理落地.md`
> 本规则从 2026-05-31 起生效，后续 Code Review 强制检查

---

## 禁止项

1. **禁止**在 `services/*/src/main/java/**/client/dto/` 下新增跨服务 DTO 复制。跨服务调用的 DTO/VO 必须放入对应 `shared/*-api` 模块。

2. **禁止**在业务服务中新增本地 `GlobalExceptionHandler.java`。统一异常处理由 `psm-common-web` 承载，各业务服务只需引入依赖即可。

3. **禁止**业务服务之间在 POM 中直接依赖其他业务服务的 artifact。跨服务调用只能通过 `shared/*-api` 模块中的 Feign Client 接口。

4. **禁止**业务服务直连其他服务的数据库。跨服务数据访问只能通过 API 调用。

## 必须项

1. 跨服务调用契约**必须**放入对应 `shared/*-api` 模块。选择规则如下：
   - 作业票/承包商 → `psm-operation-api`
   - 报警/定位/视频 → `psm-realtime-api`
   - 审计/通知/文件 → `psm-platform-api`
   - 风险/重大危险源/巡检 → `psm-risk-api`
   - MOC/PHA/PSSR/Barrier → `psm-process-safety-api`
   - 事故/CAPA/治理/报表 → `psm-incident-governance-api`
   - 用户/角色/权限 → `psm-identity-api`

2. 新增服务间 DTO/VO/Client **必须**进入对应业务域 API 模块。

3. `psm-mobile-bff` 新增接口**必须**依赖 API 模块，不得本地复制领域 DTO/VO。

4. `*-api` 模块**只能**包含：DTO、VO、枚举、Request/Response 对象、Feign Client 接口。**不能**包含：Entity、Mapper、ServiceImpl、Controller、数据库配置。

5. 每个 PR 的 diff 中**必须**包含受影响服务的测试通过记录。

## 例外与审批

- 紧急修复需要绕过规则时，PR 标题加 `[GOV-EXEMPT]`，并在描述中说明理由。
- 豁免只适用于单个 PR，后续必须通过正式重构消除豁免。

## API 模块与业务域映射

| API 模块 | 服务域 | 允许依赖的服务 |
|---|---|---|
| `psm-identity-api` | 身份与组织 | 所有服务 |
| `psm-platform-api` | 平台支撑 | 所有服务 |
| `psm-realtime-api` | 实时感知 | alarm, location, video, major-hazard, work-permit, mobile-bff |
| `psm-operation-api` | 作业与承包商 | work-permit, contractor, mobile-bff |
| `psm-risk-api` | 风险防控 | dual-prevention, major-hazard, inspection, alarm, work-permit |
| `psm-process-safety-api` | 流程安全 | moc, pha, pssr, barrier, incident |
| `psm-incident-governance-api` | 事件治理 | incident, governance, report |

## 审查检查点

Code Reviewer 在每次 PR 中确认：
- [ ] diff 中无新增 `GlobalExceptionHandler.java`
- [ ] diff 中无新增跨服务 DTO 复制（`client/dto/` 目录无新增文件）
- [ ] 新增 Feign Client 在合适的 `*-api` 模块中
- [ ] 业务服务 POM 不直接依赖其他业务服务 artifact
- [ ] `*-api` 模块中无 Entity/Mapper/ServiceImpl/Controller 类
