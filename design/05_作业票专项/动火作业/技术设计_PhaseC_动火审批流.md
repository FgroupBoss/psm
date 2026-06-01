# 动火作业 · Phase C 审批流技术设计

> **文档类型**：技术设计  
> **关联**：[产品设计_动火审批流.md](./产品设计_动火审批流.md) V1.2  
> **实施**：Phase C 与 Phase A/B **并行**（C1～C4）  
> **代码落点**：配置域 `psm-process-safety-api` · `configrule`；运行域 `psm-operation-api` · `workpermit`

---

## 1. 设计结论

| 项 | 方案 |
| --- | --- |
| 配置存储 | 独立表 `hot_work_workflow_*` 落在 **`psm_process_safety`** 库（配置域），不复用 `config_item.content_json` 存节点，便于节点 CRUD 与发布快照 |
| 跨域调用 | 与现网一致：**RestTemplate Client + 共享 DTO**（非 OpenFeign）；契约以 Java Interface + DTO 形式定义，后续可抽 Feign |
| 主票状态机 | `WorkPermitStatus` **不变**；`APPROVING` 期间由 **审批子状态机**（instance/node/task）驱动 |
| 兼容 | 非动火票 / 动火未选模板 / 特性开关关闭 → 走 M06 单节点 `approve` |
| 切换 | `psm.hot-work.multi-node-approval.enabled=true` 且 `workType=HOT_WORK` 且 `workflowTemplateId!=null` 时启用多节点引擎 |

> **服务映射**：产品设计中的「配置服务」对应治理后 **`psm-process-safety-api:18111`**（`/api/config` 已存在）；作业票对应 **`psm-operation-api:18088`**。

---

## 2. 模块边界

```text
psm-process-safety-api (18111)                psm-operation-api (18088)
┌──────────────────────────────┐            ┌──────────────────────────────┐
│ configrule.hotwork           │  HTTP      │ workpermit                   │
│  · HotWorkWorkflowController │ ◀───────── │  · HotWorkWorkflowClient     │
│  · HotWorkWorkflowService    │            │  · HotWorkApprovalEngine     │
│  · ApproverResolver          │            │  · WorkPermitService (改造)  │
│  · DDL: hot_work_workflow_*  │            │  · DDL: permit_approval_*    │
└──────────────────────────────┘            └──────────────────────────────┘
         psm_process_safety                          psm_operation
```

---

## 3. DDL 拆分

### 3.1 配置域 · `psm_process_safety`

脚本路径：`psm-process-safety-api/src/main/resources/db/hotwork/V1__hot_work_workflow.sql`

```sql
-- 动火审批流模板
CREATE TABLE IF NOT EXISTS hot_work_workflow_template (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id       BIGINT       NOT NULL COMMENT '租户ID',
  template_code   VARCHAR(64)  NOT NULL COMMENT '模板编码',
  template_name   VARCHAR(128) NOT NULL COMMENT '模板名称',
  hot_work_level  VARCHAR(32)  NOT NULL COMMENT 'SPECIAL/LEVEL_1/LEVEL_2/ALL',
  area_scope_type VARCHAR(16)  NOT NULL DEFAULT 'ALL' COMMENT 'ALL/SPECIFIED',
  area_ids_json   VARCHAR(2048) NULL COMMENT '区域ID JSON数组',
  version_no      INT          NOT NULL DEFAULT 1 COMMENT '版本号',
  status          VARCHAR(16)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/DISABLED',
  snapshot_json   MEDIUMTEXT   NULL COMMENT '发布时节点快照',
  remark          VARCHAR(512) NULL,
  created_by      VARCHAR(128) NULL,
  updated_by      VARCHAR(128) NULL,
  created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted         TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_hw_wf_tpl (tenant_id, template_code, version_no, deleted),
  KEY idx_hw_wf_tpl_query (tenant_id, status, hot_work_level, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动火审批流模板';

-- 动火审批流节点（草稿编辑态；发布后以 snapshot_json 为准）
CREATE TABLE IF NOT EXISTS hot_work_workflow_node (
  id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id            BIGINT       NOT NULL COMMENT '租户ID',
  template_id          BIGINT       NOT NULL COMMENT '模板ID',
  node_seq             INT          NOT NULL COMMENT '节点序号从1开始',
  node_name            VARCHAR(128) NOT NULL COMMENT '节点名称',
  sign_mode            VARCHAR(8)   NOT NULL COMMENT 'ANY=或签 ALL=会签',
  approver_rule_type   VARCHAR(32)  NOT NULL COMMENT '审批人规则类型',
  approver_rule_value  VARCHAR(2048) NOT NULL COMMENT '规则参数JSON',
  timeout_hours        INT          NOT NULL DEFAULT 0 COMMENT '超时小时，0不提醒',
  required             TINYINT      NOT NULL DEFAULT 1 COMMENT '是否必经',
  created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted              TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_hw_wf_node (tenant_id, template_id, node_seq, deleted),
  KEY idx_hw_wf_node_tpl (tenant_id, template_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动火审批流节点';
```

**`snapshot_json` 结构（发布时写入）**

```json
{
  "templateId": 1,
  "templateCode": "DH-L2-STD",
  "templateName": "二级动火-标准审批",
  "versionNo": 2,
  "hotWorkLevel": "LEVEL_2",
  "areaScopeType": "ALL",
  "areaIds": [],
  "nodes": [
    {
      "nodeSeq": 1,
      "nodeName": "车间主任",
      "signMode": "ANY",
      "approverRuleType": "ROLE",
      "approverRuleValue": "{\"roleCode\":\"AREA_MANAGER\"}",
      "timeoutHours": 24,
      "required": true
    }
  ]
}
```

### 3.2 运行域 · `psm_operation`

脚本路径：`psm-operation-api/src/main/resources/db/workpermit/V2__hot_work_approval.sql`

```sql
-- work_permit 扩展
ALTER TABLE work_permit
  ADD COLUMN hot_work_level           VARCHAR(32)  NULL COMMENT '动火级别 SPECIAL/LEVEL_1/LEVEL_2' AFTER work_type,
  ADD COLUMN workflow_template_id     BIGINT       NULL COMMENT '所选动火审批流模板ID' AFTER hot_work_level,
  ADD COLUMN workflow_template_version INT         NULL COMMENT '所选模板版本' AFTER workflow_template_id,
  ADD COLUMN workflow_template_name   VARCHAR(128) NULL COMMENT '模板名称冗余' AFTER workflow_template_version,
  ADD KEY idx_wp_hot_workflow (tenant_id, workflow_template_id);

-- 审批实例
CREATE TABLE IF NOT EXISTS permit_approval_instance (
  id                     BIGINT       NOT NULL AUTO_INCREMENT,
  tenant_id              BIGINT       NOT NULL,
  work_permit_id         BIGINT       NOT NULL,
  workflow_template_id   BIGINT       NOT NULL,
  workflow_template_version INT       NOT NULL,
  template_snapshot_json MEDIUMTEXT   NOT NULL COMMENT '提交时快照',
  status                 VARCHAR(16)  NOT NULL COMMENT 'RUNNING/COMPLETED/CANCELLED',
  current_node_seq       INT          NULL COMMENT '当前节点序号',
  started_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at           DATETIME     NULL,
  created_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted                TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_pai_permit (tenant_id, work_permit_id, deleted),
  KEY idx_pai_running (tenant_id, status, current_node_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动火审批实例';

CREATE TABLE IF NOT EXISTS permit_approval_node_instance (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  tenant_id        BIGINT       NOT NULL,
  instance_id      BIGINT       NOT NULL,
  node_seq         INT          NOT NULL,
  node_name        VARCHAR(128) NOT NULL,
  sign_mode        VARCHAR(8)   NOT NULL COMMENT 'ANY/ALL',
  status           VARCHAR(16)  NOT NULL COMMENT 'PENDING/APPROVED/REJECTED/RETURNED/SKIPPED',
  required_count   INT          NOT NULL DEFAULT 1 COMMENT '或签=1 会签=N',
  approved_count   INT          NOT NULL DEFAULT 0,
  started_at       DATETIME     NULL,
  completed_at     DATETIME     NULL,
  created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted          TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_pani_inst_seq (tenant_id, instance_id, node_seq, deleted),
  KEY idx_pani_inst (tenant_id, instance_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动火审批节点实例';

CREATE TABLE IF NOT EXISTS permit_approval_task (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  tenant_id         BIGINT       NOT NULL,
  node_instance_id  BIGINT       NOT NULL,
  work_permit_id    BIGINT       NOT NULL COMMENT '冗余便于待办查询',
  assignee_user_id  BIGINT       NOT NULL,
  assignee_name     VARCHAR(128) NULL,
  status            VARCHAR(16)  NOT NULL COMMENT 'PENDING/APPROVED/REJECTED/RETURNED/CANCELLED',
  action            VARCHAR(16)  NULL COMMENT '实际动作',
  opinion           VARCHAR(1024) NULL,
  acted_at          DATETIME     NULL,
  created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted           TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_pat_assignee (tenant_id, assignee_user_id, status, deleted),
  KEY idx_pat_permit (tenant_id, work_permit_id, status, deleted),
  KEY idx_pat_node (tenant_id, node_instance_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动火审批任务';

-- 扩展审批记录（兼容 timeline）
ALTER TABLE permit_approval_record
  ADD COLUMN instance_id       BIGINT       NULL AFTER work_permit_id,
  ADD COLUMN node_instance_id  BIGINT       NULL AFTER instance_id,
  ADD COLUMN node_seq          INT          NULL AFTER node_instance_id,
  ADD COLUMN node_name         VARCHAR(128) NULL AFTER node_seq,
  ADD COLUMN sign_mode         VARCHAR(8)   NULL AFTER node_name,
  ADD COLUMN task_id           BIGINT       NULL AFTER sign_mode,
  ADD COLUMN assignee_user_id  BIGINT       NULL AFTER task_id;
```

### 3.3 DDL 执行顺序

| 顺序 | 库 | 脚本 | 批次 |
| --- | --- | --- | --- |
| 1 | psm_process_safety | `V1__hot_work_workflow.sql` | C1 |
| 2 | psm_operation | `V2__hot_work_approval.sql` | C3 |
| 3 | psm_operation | 种子数据：测试模板 + 菜单（可选） | C2 |

---

## 4. 跨域契约（Feign 等价 · RestTemplate）

> 现网跨域使用 **RestTemplate**，无 `@FeignClient`。契约定义为 **Interface + DTO**，实现类 `HotWorkWorkflowClient`。

### 4.1 DTO 包路径

| 侧 | 包路径 |
| --- | --- |
| 契约 DTO（消费方持有） | `com.fgroupboss.ai.psm.operation.client.dto.hotwork.*` |
| 配置域 VO/Request | `com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.*` |

### 4.2 契约 Interface

```java
/**
 * 动火审批流配置域契约（operation → process-safety）。
 * 实现：HotWorkWorkflowClient @Component + RestTemplate。
 */
public interface HotWorkWorkflowApi {

    /** 创建页可选模板列表 */
    List<HotWorkWorkflowSummaryDTO> listAvailable(HotWorkWorkflowQueryDTO query);

    /** 校验模板可选用 */
    HotWorkWorkflowValidateResultDTO validateSelection(HotWorkWorkflowValidateDTO req);

    /** 按 id+version 取发布快照（submit 时用） */
    HotWorkWorkflowSnapshotDTO getSnapshot(Long tenantId, Long templateId, Integer versionNo);

    /** 审批人试算（配置端 + submit 前预检） */
    HotWorkApproverResolveResultDTO resolveApprovers(HotWorkApproverResolveDTO req);
}
```

### 4.3 REST 映射（process-safety 提供）

| 契约方法 | HTTP | Path |
| --- | --- | --- |
| listAvailable | GET | `/api/config/hot-work/workflows/available` |
| validateSelection | POST | `/api/config/hot-work/workflows/validate-selection` |
| getSnapshot | GET | `/api/config/hot-work/workflows/{id}/snapshot?tenantId=&versionNo=` |
| resolveApprovers | POST | `/api/config/hot-work/workflows/resolve-approvers` |

**配置端 CRUD（C1，不走 Client）**

| HTTP | Path |
| --- | --- |
| GET/POST | `/api/config/hot-work/workflows` |
| GET/PUT | `/api/config/hot-work/workflows/{id}` |
| GET/PUT | `/api/config/hot-work/workflows/{id}/nodes` |
| POST | `/api/config/hot-work/workflows/{id}/publish` |
| POST | `/api/config/hot-work/workflows/{id}/disable` |

### 4.4 核心 DTO 字段

**HotWorkWorkflowQueryDTO**

```java
Long tenantId;
String hotWorkLevel;   // SPECIAL / LEVEL_1 / LEVEL_2
Long areaId;
String status = "PUBLISHED";
```

**HotWorkWorkflowSnapshotDTO**

```java
Long templateId;
Integer versionNo;
String templateCode;
String templateName;
String hotWorkLevel;
List<HotWorkWorkflowNodeSnapshotDTO> nodes;
```

**HotWorkWorkflowNodeSnapshotDTO**

```java
Integer nodeSeq;
String nodeName;
String signMode;           // ANY / ALL
String approverRuleType;
String approverRuleValue;  // JSON string
Integer timeoutHours;
Boolean required;
```

**HotWorkApproverResolveDTO**

```java
Long tenantId;
Long workPermitId;         // 可选，有则读票字段
String hotWorkLevel;
Long areaId;
Long unitId;
Long permitIssuerUserId;
Long supervisorUserId;
Long contractorCompanyId;
HotWorkWorkflowNodeSnapshotDTO node;
```

**HotWorkApproverResolveResultDTO**

```java
boolean resolved;
List<HotWorkApproverDTO> approvers;  // userId, userName, roleLabel
String failReason;
```

### 4.5 Client 实现要点

路径：`psm-operation-api/.../workpermit/client/HotWorkWorkflowClient.java`

```java
@Component
public class HotWorkWorkflowClient implements HotWorkWorkflowApi {
    // psm.process-safety-service-url 默认 http://psm-process-safety:18111
    // 失败：抛 RestClientException → WorkPermitService 转 UPSTREAM_UNAVAILABLE
}
```

**application.yml 新增（operation）**

```yaml
psm:
  process-safety-service-url: ${PSM_PROCESS_SAFETY_SERVICE_URL:http://psm-process-safety:18111}
  hot-work:
    multi-node-approval:
      enabled: ${PSM_HOT_WORK_MULTI_APPROVAL:false}
```

### 4.6 审批人解析器（process-safety）

`ApproverResolver` 策略：

| ruleType | 实现 |
| --- | --- |
| FIXED_USERS | 解析 JSON userIds |
| ROLE | 调 identity/master-data HTTP 或预留 IAM 接口查区域角色 |
| ORG_POST | 查岗位持有人 |
| FORM_FIELD | 从 ResolveDTO 字段取值 |
| DEPT_LEADER | 按 unitId 查部门负责人 |

一期可 **ROLE + FORM_FIELD + FIXED_USERS** 先落地；其余返回「未实现」并在配置试算暴露。

---

## 5. 状态机改造

### 5.1 主票状态机（不变）

```text
DRAFT/RETURNED ──submit──▶ APPROVING ──(全部审批节点完成)──▶ PENDING_SITE_PERMIT
APPROVING ──reject──▶ CLOSED
APPROVING ──return──▶ RETURNED
```

`WorkPermitStatusTransition.approveTarget` **仅在审批实例 COMPLETED 时** 调用，不再在单次 `approve` 中直接触发（多节点模式）。

### 5.2 审批子状态机（新增）

```text
[实例 RUNNING]
  → 激活 nodeSeq=N (node_instance=PENDING)
  → 创建 tasks (PENDING)
  → ANY: 任一 task APPROVE → node APPROVED → 下一节点或实例 COMPLETED
  → ALL: 全部 task APPROVE → node APPROVED → 下一节点或实例 COMPLETED
  → 任一 task REJECT → 实例 CANCELLED + 主票 CLOSED
  → 任一 task RETURN → 实例 CANCELLED + 主票 RETURNED
```

### 5.3 类职责拆分

| 类 | 职责 |
| --- | --- |
| `HotWorkApprovalEngine` | 实例化、激活节点、或签/会签通过判定、流转下一节点、完结调主票 transition |
| `HotWorkApprovalTaskService` | 待办查询、任务状态 CAS 更新 |
| `WorkPermitServiceImpl` | 路由：多节点 vs legacy；submit/approve/return/reject 委托 Engine |
| `WorkPermitStatusTransition` | 保持纯主票转换，不感知节点 |

### 5.4 `approve` API 改造

**请求扩展 `PermitActionRequest`**

```java
private Long approvalTaskId;  // 多节点模式必填
private String action;        // APPROVE / RETURN / REJECT
private String opinion;
```

**路由伪代码**

```java
if (useMultiNodeApproval(permit)) {
    return hotWorkApprovalEngine.actOnTask(tenantId, permitId, request, operator);
}
return legacyApprove(...);  // 现 M06 单节点
```

### 5.5 `submit` 改造

```java
if (useMultiNodeApproval(permit)) {
  validateWorkflowSelected(permit);
  snapshot = hotWorkWorkflowClient.getSnapshot(...);
  for (node : snapshot.nodes) {
    resolve = hotWorkWorkflowClient.resolveApprovers(...);
    if (!resolve.resolved || resolve.approvers.isEmpty())
      throw 409 "节点【{nodeName}】未找到审批人";
  }
  hotWorkApprovalEngine.startInstance(permit, snapshot);
  transition(DRAFT→APPROVING);
} else {
  legacySubmit(...);
}
```

### 5.6 RETURNED 再提交

- 旧 `RUNNING/CANCELLED` instance 保留审计。
- 新建 instance，`current_node_seq=1`，任务全部重建。

### 5.7 并发（或签）

```sql
UPDATE permit_approval_task
SET status='APPROVED', action='APPROVE', acted_at=NOW(), ...
WHERE id=? AND tenant_id=? AND status='PENDING';
-- affectedRows=0 → 409 已被他人处理
```

节点级 CAS：

```sql
UPDATE permit_approval_node_instance
SET status='APPROVED', approved_count=approved_count+1, ...
WHERE id=? AND status='PENDING' AND sign_mode='ANY';
```

---

## 6. 作业域 API 增量

| 接口 | 方法 | 说明 |
| --- | --- | --- |
| `/api/work-permits/hot-work/available-workflows` | GET | 聚合配置域 available 列表 |
| `/api/work-permits` POST/PUT | | 增加 `hotWorkLevel`、`workflowTemplateId` |
| `/api/work-permits/{id}/submit` | POST | 多节点实例化 |
| `/api/work-permits/{id}/approval/progress` | GET | 节点条 + 会签计数 |
| `/api/work-permits/{id}/approval/tasks/mine` | GET | 当前用户待办 |
| `/api/work-permits/{id}/approve` | POST | 增加 taskId；多节点路由 |
| `/api/work-permits/{id}/return` | POST | 同上 |
| `/api/work-permits/{id}/reject` | POST | 任一级可驳回 |

---

## 7. 任务清单（开发拆分）

### C1 · 配置域基础（process-safety，约 1～1.5 周）

| 任务 ID | 任务 | 产出 |
| --- | --- | --- |
| T-C-101 | 执行 `V1__hot_work_workflow.sql` | 表就绪 |
| T-C-102 | Entity/Mapper `HotWorkWorkflowTemplate/Node` | MyBatis-Plus |
| T-C-103 | `HotWorkWorkflowService` CRUD + 节点顺序校验 | 服务 |
| T-C-104 | `publish`：写 snapshot_json、status=PUBLISHED、version_no+1 | 发布 |
| T-C-105 | `disable` + 审计 `config_audit_change_log` | 停用 |
| T-C-106 | `HotWorkWorkflowController` CRUD/publish/disable | REST |
| T-C-107 | 网关路由 `/api/config/hot-work/**` → 18111 | 网关 |

### C2 · 选流 + 契约（并行，约 1 周）

| 任务 ID | 任务 | 产出 |
| --- | --- | --- |
| T-C-201 | DTO 契约 `operation.client.dto.hotwork.*` | 契约包 |
| T-C-202 | `HotWorkWorkflowClient` + yml URL | Client |
| T-C-203 | `listAvailable` / `validateSelection` / `getSnapshot` API | 配置 REST |
| T-C-204 | `ApproverResolver`（FIXED/ROLE/FORM_FIELD） | 解析器 |
| T-C-205 | `resolveApprovers` API | REST |
| T-C-206 | operation `available-workflows` 聚合接口 | BFF |
| T-C-207 | `WorkPermitRequest/Entity` 增字段 + 创建校验 | 选流必填 |
| T-C-208 | 前端：动火创建页审批流下拉+预览（可与 A 并行） | UI |

### C3 · 审批引擎（operation，约 1.5～2 周）

| 任务 ID | 任务 | 产出 |
| --- | --- | --- |
| T-C-301 | 执行 `V2__hot_work_approval.sql` | 表就绪 |
| T-C-302 | Entity/Mapper instance/node/task | 持久层 |
| T-C-303 | `HotWorkApprovalEngine.startInstance` | 实例化 |
| T-C-304 | `activateNode` + 创建 tasks | 激活 |
| T-C-305 | `actOnTask` ANY 逻辑 + 兄弟任务 CANCELLED | 或签 |
| T-C-306 | `actOnTask` ALL 逻辑 + approved_count | 会签 |
| T-C-307 | REJECT/RETURN → 实例 CANCELLED + 主票 transition | 驳回任一级 |
| T-C-308 | 末节点完成 → `approveTarget` → PENDING_SITE_PERMIT | 完结 |
| T-C-309 | 改造 `WorkPermitServiceImpl.submit/approve/return/reject` | 路由 |
| T-C-310 | 特性开关 + legacy 分支回归 | 兼容 |
| T-C-311 | 扩展 `permit_approval_record` 写入 | timeline |
| T-C-312 | `approval/progress` API | 进度 |
| T-C-313 | 与 Phase B 联调：submit 交底门槛（若 B 已合并） | 联调 |

### C4 · 待办与 UI（约 1 周，与 A 联调）

| 任务 ID | 任务 | 产出 |
| --- | --- | --- |
| T-C-401 | `approval/tasks/mine` + 待办列表 | 待办 |
| T-C-402 | 详情页审批进度条 + 会签 n/m | Web UI |
| T-C-403 | 审批操作按钮（带 taskId） | Web UI |
| T-C-404 | `flow-progress` 审批中子进度（与 Phase A 汇合） | API+UI |
| T-C-405 | 通知模板：动火审批待办（可选） | 通知 |
| T-C-406 | E2E：二级动火 3 节点或签/会签混合 | 验收 |

---

## 8. 测试要点

| 用例 | 预期 |
| --- | --- |
| 动火未选 workflow 保存 | 400 |
| 非动火不选 workflow | 200，走 legacy |
| 开关 false 的动火 | submit 后单节点 approve 仍可用 |
| ANY 两人同时 approve | 一人 200，一人 409 |
| ALL 2/3 approve | 节点仍 PENDING；3/3 后过节点 |
| 第 1 节点 reject | CLOSED + 实例 CANCELLED |
| 中间节点 reject | 同上（任一级可驳回） |
| 末节点 approve | PENDING_SITE_PERMIT |
| RETURNED 再 submit | 新 instance，从节点 1 开始 |
| 配置服务不可用 | submit 409 UPSTREAM_UNAVAILABLE |
| 模板停用后草稿提交 | 409 请改选审批流 |

---

## 9. 上线与回滚

| 项 | 说明 |
| --- | --- |
| 灰度 | `psm.hot-work.multi-node-approval.enabled` 默认 `false` |
| 切换 | 试点租户先开 true；观察待办与 409 率 |
| 回滚 | 关开关即回 M06 单节点；已 RUNNING 实例需人工处理或脚本 CANCELLED |
| 配置 | 先 publish 至少一条 LEVEL_2 模板再开开关 |

---

## 10. 验收映射

| 产品 AC | 技术任务 |
| --- | --- |
| AC-DH-WF-01～02 | T-C-207, T-C-208 |
| AC-DH-WF-03 | T-C-303, T-C-309 |
| AC-DH-WF-04 | T-C-305 |
| AC-DH-WF-05 | T-C-306 |
| AC-DH-WF-06 | T-C-308 |
| AC-DH-WF-07 | T-C-307 |
| AC-DH-WF-13 | T-C-307 |
| AC-DH-WF-14 | §3.1 独立配置表 + Client 只读快照 |

---

*文档版本：V1.0 · 2026-06-01*
