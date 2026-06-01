-- 动火审批流模板（配置域 psm_process_safety）— 暂不自动执行，手工迁移时运行
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
  snapshot_json   MEDIUMTEXT   NULL COMMENT '发布快照',
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

CREATE TABLE IF NOT EXISTS hot_work_workflow_node (
  id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id            BIGINT       NOT NULL COMMENT '租户ID',
  template_id          BIGINT       NOT NULL COMMENT '模板ID',
  node_seq             INT          NOT NULL COMMENT '节点序号',
  node_name            VARCHAR(128) NOT NULL COMMENT '节点名称',
  sign_mode            VARCHAR(8)   NOT NULL COMMENT 'ANY/ALL',
  approver_rule_type   VARCHAR(32)  NOT NULL COMMENT '审批人规则',
  approver_rule_value  VARCHAR(2048) NOT NULL COMMENT '规则JSON',
  timeout_hours        INT          NOT NULL DEFAULT 0,
  required             TINYINT      NOT NULL DEFAULT 1,
  created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted              TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_hw_wf_node (tenant_id, template_id, node_seq, deleted),
  KEY idx_hw_wf_node_tpl (tenant_id, template_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动火审批流节点';
