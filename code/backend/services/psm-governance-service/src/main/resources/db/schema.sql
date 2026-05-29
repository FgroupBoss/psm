create table if not exists gov_template (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  template_code varchar(64) not null comment '模板编码',
  template_name varchar(255) not null comment '模板名称',
  template_type varchar(32) not null comment '模板类型',
  status varchar(32) not null default 'ACTIVE' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_gov_template_code (tenant_id, template_code, deleted),
  key idx_gov_template_tenant (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='集团模板';

create table if not exists gov_template_version (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  template_id bigint not null comment '模板ID',
  version_no varchar(32) not null comment '版本号',
  content text null comment '内容',
  status varchar(32) not null default 'DRAFT' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_gov_template_version_tpl (tenant_id, template_id)
) engine=InnoDB default charset=utf8mb4 comment='模板版本';

create table if not exists gov_site_mapping (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  site_code varchar(64) not null comment '基地编码',
  site_name varchar(255) not null comment '基地名称',
  org_id bigint null comment '组织ID',
  enabled_flag tinyint not null default 1 comment '启用',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_gov_site_code (tenant_id, site_code, deleted),
  key idx_gov_site_tenant (tenant_id, enabled_flag)
) engine=InnoDB default charset=utf8mb4 comment='基地映射';

create table if not exists gov_metric_definition (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  metric_code varchar(64) not null comment '指标编码',
  metric_name varchar(255) not null comment '指标名称',
  metric_domain varchar(32) not null comment '指标域',
  statistic_period varchar(16) not null comment '统计周期',
  formula_version varchar(32) null comment '公式版本',
  target_value decimal(18,4) null comment '目标值',
  owner_org_id bigint null comment '责任组织',
  enabled_flag tinyint not null default 1 comment '启用',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_gov_metric_code (tenant_id, metric_code, deleted),
  key idx_gov_metric_domain (tenant_id, metric_domain)
) engine=InnoDB default charset=utf8mb4 comment='指标定义';

create table if not exists gov_metric_snapshot (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  metric_code varchar(64) not null comment '指标编码',
  site_id bigint not null comment '基地ID',
  period_start datetime not null comment '周期开始',
  period_end datetime not null comment '周期结束',
  metric_value decimal(18,4) null comment '指标值',
  target_value decimal(18,4) null comment '目标值',
  calculation_time datetime null comment '计算时间',
  input_hash varchar(64) null comment '输入摘要',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_gov_metric_snapshot_query (tenant_id, metric_code, site_id, period_start)
) engine=InnoDB default charset=utf8mb4 comment='指标快照';

create table if not exists gov_audit_issue (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  issue_no varchar(64) not null comment '问题编号',
  site_id bigint not null comment '基地ID',
  description varchar(2048) not null comment '描述',
  owner_user_id bigint null comment '责任人',
  due_at datetime null comment '截止时间',
  status varchar(32) not null default 'PENDING_ASSIGN' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_gov_audit_issue_site (tenant_id, site_id, status)
) engine=InnoDB default charset=utf8mb4 comment='审计问题';

create table if not exists dw_work_permit_fact (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  site_id bigint not null comment '基地ID',
  permit_no varchar(64) not null comment '作业票号',
  permit_type varchar(32) null comment '类型',
  status varchar(32) null comment '状态',
  occurred_at datetime null comment '发生时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_dw_work_permit_site (tenant_id, site_id)
) engine=InnoDB default charset=utf8mb4 comment='作业事实表';

create table if not exists dw_alarm_fact (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  site_id bigint not null comment '基地ID',
  alarm_no varchar(64) not null comment '报警号',
  alarm_level varchar(32) null comment '等级',
  status varchar(32) null comment '状态',
  occurred_at datetime null comment '发生时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_dw_alarm_site (tenant_id, site_id)
) engine=InnoDB default charset=utf8mb4 comment='报警事实表';

create table if not exists dw_hazard_fact (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  site_id bigint not null comment '基地ID',
  hazard_no varchar(64) not null comment '隐患号',
  hazard_level varchar(32) null comment '等级',
  status varchar(32) null comment '状态',
  occurred_at datetime null comment '发生时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_dw_hazard_site (tenant_id, site_id)
) engine=InnoDB default charset=utf8mb4 comment='隐患事实表';

create table if not exists dw_moc_fact (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  site_id bigint not null comment '基地ID',
  change_no varchar(64) not null comment '变更号',
  change_level varchar(32) null comment '等级',
  status varchar(32) null comment '状态',
  occurred_at datetime null comment '发生时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_dw_moc_site (tenant_id, site_id)
) engine=InnoDB default charset=utf8mb4 comment='变更事实表';

create table if not exists dw_incident_fact (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  site_id bigint not null comment '基地ID',
  incident_no varchar(64) not null comment '事件号',
  incident_level varchar(32) null comment '等级',
  status varchar(32) null comment '状态',
  occurred_at datetime null comment '发生时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_dw_incident_site (tenant_id, site_id)
) engine=InnoDB default charset=utf8mb4 comment='事故事实表';
