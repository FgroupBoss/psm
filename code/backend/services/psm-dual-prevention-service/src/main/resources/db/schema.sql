create table if not exists dp_risk_unit (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  parent_id bigint null comment '上级单元ID',
  unit_code varchar(128) not null comment '单元编码',
  unit_name varchar(255) not null comment '单元名称',
  area_id bigint null comment '所属区域',
  equipment_id bigint null comment '关联设备',
  major_hazard_id bigint null comment '关联重大危险源',
  inherent_risk_level varchar(32) null comment '固有风险等级',
  residual_risk_level varchar(32) null comment '残余风险等级',
  owner_org_id bigint null comment '责任部门',
  owner_user_id bigint null comment '责任人',
  status varchar(32) not null default 'DRAFT' comment '草稿/生效/停用',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_dp_risk_unit_code (tenant_id, unit_code, deleted),
  key idx_dp_risk_unit_tenant (tenant_id, status),
  key idx_dp_risk_unit_area (tenant_id, area_id),
  key idx_dp_risk_unit_parent (tenant_id, parent_id)
) engine=InnoDB default charset=utf8mb4 comment='风险单元';

create table if not exists dp_risk_event (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  risk_unit_id bigint not null comment '风险单元ID',
  event_code varchar(128) not null comment '事件编码',
  event_name varchar(255) not null comment '事件名称',
  hazard_factors varchar(1024) null comment '致险因素',
  possible_consequence varchar(1024) null comment '可能后果',
  inherent_risk_level varchar(32) null comment '固有风险等级',
  status varchar(32) not null default 'ACTIVE' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_dp_risk_event_unit (tenant_id, risk_unit_id),
  key idx_dp_risk_event_code (tenant_id, event_code)
) engine=InnoDB default charset=utf8mb4 comment='风险事件';

create table if not exists dp_control_measure (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  risk_event_id bigint not null comment '风险事件ID',
  measure_type varchar(32) not null comment '措施类型',
  measure_content varchar(1024) not null comment '措施内容',
  responsible_post varchar(128) null comment '责任岗位',
  check_cycle_days int null comment '检查周期(天)',
  status varchar(32) not null default 'ACTIVE' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_dp_control_measure_event (tenant_id, risk_event_id)
) engine=InnoDB default charset=utf8mb4 comment='管控措施';

create table if not exists dp_hazard_report (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  hazard_no varchar(64) not null comment '隐患编号',
  hazard_level varchar(32) not null comment '隐患等级',
  source_type varchar(32) not null comment '来源类型',
  source_biz_id bigint null comment '来源业务ID',
  risk_unit_id bigint null comment '关联风险单元',
  area_id bigint null comment '所属区域',
  description varchar(2048) not null comment '隐患描述',
  found_at datetime not null comment '发现时间',
  rectification_deadline datetime null comment '整改期限',
  status varchar(32) not null default 'PENDING_CONFIRM' comment '隐患状态',
  overdue_flag tinyint not null default 0 comment '是否逾期',
  assignee_org_id bigint null comment '整改责任部门',
  assignee_user_id bigint null comment '整改责任人',
  contractor_id bigint null comment '承包商ID',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_dp_hazard_no (tenant_id, hazard_no, deleted),
  key idx_dp_hazard_status (tenant_id, status),
  key idx_dp_hazard_overdue (tenant_id, overdue_flag, status),
  key idx_dp_hazard_area (tenant_id, area_id),
  key idx_dp_hazard_unit (tenant_id, risk_unit_id)
) engine=InnoDB default charset=utf8mb4 comment='隐患上报';

create table if not exists dp_hazard_action (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  hazard_id bigint not null comment '隐患ID',
  action_type varchar(32) not null comment '动作类型',
  before_status varchar(32) null comment '变更前状态',
  after_status varchar(32) null comment '变更后状态',
  content varchar(1024) null comment '处理说明',
  evidence_file_ids varchar(512) null comment '证据附件ID列表',
  operator_id bigint null comment '操作人ID',
  operator_name varchar(128) null comment '操作人姓名',
  operated_at datetime not null default current_timestamp comment '操作时间',
  primary key (id),
  key idx_dp_hazard_action_hazard (tenant_id, hazard_id)
) engine=InnoDB default charset=utf8mb4 comment='隐患处理记录';

create table if not exists dp_hazard_escalation (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  hazard_id bigint not null comment '隐患ID',
  reason varchar(512) null comment '升级原因',
  notify_user_id bigint null comment '通知用户ID',
  operator_name varchar(128) null comment '操作人',
  escalated_at datetime not null default current_timestamp comment '升级时间',
  primary key (id),
  key idx_dp_hazard_escalation_hazard (tenant_id, hazard_id, escalated_at)
) engine=InnoDB default charset=utf8mb4 comment='隐患逾期升级记录';
