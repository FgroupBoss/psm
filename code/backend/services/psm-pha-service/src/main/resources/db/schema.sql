create table if not exists pha_project (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  project_no varchar(64) not null comment '项目编号',
  project_name varchar(255) not null comment '项目名称',
  site_id bigint null comment '基地ID',
  unit_id bigint null comment '装置ID',
  major_hazard_id bigint null comment '重大危险源ID',
  method varchar(32) null comment '分析方法',
  version varchar(32) null comment '版本',
  review_due_at datetime null comment '复审时间',
  status varchar(32) not null default 'DRAFT' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pha_project_tenant (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='PHA项目';

create table if not exists pha_node (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  project_id bigint not null comment '项目ID',
  node_no varchar(64) not null comment '节点编号',
  node_name varchar(255) not null comment '节点名称',
  design_intent varchar(1024) null comment '设计意图',
  parameters varchar(512) null comment '参数',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pha_node_project (tenant_id, project_id)
) engine=InnoDB default charset=utf8mb4 comment='HAZOP节点';

create table if not exists hazop_deviation (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  node_id bigint not null comment '节点ID',
  parameter varchar(64) null comment '参数',
  guideword varchar(64) null comment '引导词',
  deviation_desc varchar(1024) null comment '偏差描述',
  risk_level varchar(32) null comment '风险等级',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_hazop_deviation_node (tenant_id, node_id)
) engine=InnoDB default charset=utf8mb4 comment='偏差分析';

create table if not exists hazop_cause (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  deviation_id bigint not null comment '偏差ID',
  cause_desc varchar(1024) null comment '原因描述',
  frequency varchar(32) null comment '频率',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_hazop_cause_deviation (tenant_id, deviation_id)
) engine=InnoDB default charset=utf8mb4 comment='偏差原因';

create table if not exists hazop_consequence (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  deviation_id bigint not null comment '偏差ID',
  consequence_desc varchar(1024) null comment '后果描述',
  severity varchar(32) null comment '严重度',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_hazop_consequence_deviation (tenant_id, deviation_id)
) engine=InnoDB default charset=utf8mb4 comment='后果';

create table if not exists hazop_safeguard (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  deviation_id bigint not null comment '偏差ID',
  safeguard_type varchar(32) null comment '保护层类型',
  safeguard_desc varchar(1024) null comment '保护层描述',
  effectiveness varchar(32) null comment '有效性',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_hazop_safeguard_deviation (tenant_id, deviation_id)
) engine=InnoDB default charset=utf8mb4 comment='保护层';

create table if not exists pha_recommendation (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  project_id bigint null comment '项目ID',
  deviation_id bigint null comment '偏差ID',
  rec_no varchar(64) not null comment '建议项编号',
  description varchar(2048) not null comment '描述',
  owner_user_id bigint null comment '责任人',
  due_at datetime null comment '期限',
  status varchar(32) not null default 'PENDING_ASSIGN' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pha_recommendation_tenant (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='建议项';

create table if not exists lopa_scenario (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  scenario_no varchar(64) not null comment '场景编号',
  project_id bigint null comment '项目ID',
  deviation_id bigint null comment '偏差ID',
  initiating_event_frequency decimal(18,8) null comment '初始事件频率',
  consequence_severity varchar(32) null comment '后果严重度',
  target_frequency decimal(18,8) null comment '目标频率',
  mitigated_frequency decimal(18,8) null comment '保护后频率',
  sil_recommendation varchar(32) null comment 'SIL建议',
  calculation_version varchar(32) null comment '计算版本',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_lopa_scenario_project (tenant_id, project_id)
) engine=InnoDB default charset=utf8mb4 comment='LOPA场景';

create table if not exists lopa_ipl (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  scenario_id bigint not null comment '场景ID',
  ipl_name varchar(255) null comment 'IPL名称',
  pfd decimal(18,8) null comment 'PFD',
  ipl_type varchar(32) null comment 'IPL类型',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_lopa_ipl_scenario (tenant_id, scenario_id)
) engine=InnoDB default charset=utf8mb4 comment='独立保护层';

create table if not exists pha_review_plan (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  project_id bigint not null comment '项目ID',
  plan_at datetime null comment '计划时间',
  reviewer_user_id bigint null comment '复审人',
  status varchar(32) not null default 'PLANNED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pha_review_plan_project (tenant_id, project_id)
) engine=InnoDB default charset=utf8mb4 comment='复审计划';
