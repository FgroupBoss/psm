-- 动火审批运行表（作业域 psm_operation）— 暂不自动执行，手工迁移时运行
ALTER TABLE work_permit
  ADD COLUMN hot_work_level           VARCHAR(32)  NULL COMMENT '动火级别' AFTER work_type,
  ADD COLUMN workflow_template_id     BIGINT       NULL COMMENT '审批流模板ID' AFTER hot_work_level,
  ADD COLUMN workflow_template_version INT         NULL COMMENT '模板版本' AFTER workflow_template_id,
  ADD COLUMN workflow_template_name   VARCHAR(128) NULL COMMENT '模板名称' AFTER workflow_template_version;

CREATE TABLE IF NOT EXISTS permit_approval_instance (
  id                       BIGINT       NOT NULL AUTO_INCREMENT,
  tenant_id                BIGINT       NOT NULL,
  work_permit_id             BIGINT       NOT NULL,
  workflow_template_id     BIGINT       NOT NULL,
  workflow_template_version INT         NOT NULL,
  template_snapshot_json   MEDIUMTEXT   NOT NULL,
  status                   VARCHAR(16)  NOT NULL,
  current_node_seq         INT          NULL,
  started_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at             DATETIME     NULL,
  created_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted                  TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_pai_permit (tenant_id, work_permit_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动火审批实例';

CREATE TABLE IF NOT EXISTS permit_approval_node_instance (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  tenant_id        BIGINT       NOT NULL,
  instance_id      BIGINT       NOT NULL,
  node_seq         INT          NOT NULL,
  node_name        VARCHAR(128) NOT NULL,
  sign_mode        VARCHAR(8)   NOT NULL,
  status           VARCHAR(16)  NOT NULL,
  required_count   INT          NOT NULL DEFAULT 1,
  approved_count   INT          NOT NULL DEFAULT 0,
  started_at       DATETIME     NULL,
  completed_at     DATETIME     NULL,
  created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted          TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_pani_inst_seq (tenant_id, instance_id, node_seq, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动火审批节点实例';

CREATE TABLE IF NOT EXISTS permit_approval_task (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  tenant_id         BIGINT       NOT NULL,
  node_instance_id  BIGINT       NOT NULL,
  work_permit_id    BIGINT       NOT NULL,
  assignee_user_id  BIGINT       NOT NULL,
  assignee_name     VARCHAR(128) NULL,
  status            VARCHAR(16)  NOT NULL,
  action            VARCHAR(16)  NULL,
  opinion           VARCHAR(1024) NULL,
  acted_at          DATETIME     NULL,
  created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted           TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_pat_assignee (tenant_id, assignee_user_id, status, deleted),
  KEY idx_pat_permit (tenant_id, work_permit_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动火审批任务';

ALTER TABLE permit_approval_record
  ADD COLUMN instance_id       BIGINT       NULL,
  ADD COLUMN node_instance_id  BIGINT       NULL,
  ADD COLUMN node_seq          INT          NULL,
  ADD COLUMN node_name         VARCHAR(128) NULL,
  ADD COLUMN sign_mode         VARCHAR(8)   NULL,
  ADD COLUMN task_id           BIGINT       NULL,
  ADD COLUMN assignee_user_id  BIGINT       NULL;
