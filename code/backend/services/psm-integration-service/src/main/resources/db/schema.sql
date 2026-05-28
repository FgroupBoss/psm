create table if not exists reg_platform_config (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  platform_code varchar(64) not null comment '监管平台编码',
  platform_name varchar(255) not null comment '监管平台名称',
  base_url varchar(512) not null comment '平台基础地址',
  auth_type varchar(32) not null default 'TOKEN' comment '认证方式',
  credential_ref varchar(255) null comment '密钥引用(非明文)',
  enabled tinyint not null default 1 comment '是否启用',
  remark varchar(512) null comment '备注',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_reg_platform (tenant_id, platform_code, deleted),
  key idx_reg_platform_tenant (tenant_id, enabled)
) engine=InnoDB default charset=utf8mb4 comment='监管平台配置';

create table if not exists reg_field_mapping (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  platform_code varchar(64) not null comment '监管平台编码',
  data_domain varchar(64) not null comment '数据域',
  source_field varchar(128) not null comment '源字段',
  target_field varchar(128) not null comment '目标字段',
  transform_rule varchar(512) null comment '转换规则',
  enabled tinyint not null default 1 comment '是否启用',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_reg_field_mapping (tenant_id, platform_code, data_domain, deleted)
) engine=InnoDB default charset=utf8mb4 comment='字段映射';

create table if not exists reg_code_mapping (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  platform_code varchar(64) not null comment '监管平台编码',
  mapping_type varchar(64) not null comment '映射类型',
  source_code varchar(128) not null comment '源编码',
  target_code varchar(128) not null comment '目标编码',
  description varchar(255) null comment '说明',
  enabled tinyint not null default 1 comment '是否启用',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_reg_code_mapping (tenant_id, platform_code, mapping_type, deleted)
) engine=InnoDB default charset=utf8mb4 comment='编码对照';

create table if not exists reg_report_task (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  task_no varchar(64) not null comment '任务编号',
  platform_code varchar(64) not null comment '监管平台编码',
  data_domain varchar(64) not null comment '数据域',
  trigger_type varchar(32) not null comment '触发类型',
  data_window_start datetime null comment '数据窗口起',
  data_window_end datetime null comment '数据窗口止',
  record_count int not null default 0 comment '记录数',
  status varchar(32) not null default 'PENDING' comment '任务状态',
  payload_digest varchar(128) null comment '报文摘要',
  executed_at datetime null comment '执行时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_reg_report_task_no (tenant_id, task_no),
  key idx_reg_report_task (tenant_id, platform_code, status),
  key idx_reg_report_task_domain (tenant_id, data_domain, created_at)
) engine=InnoDB default charset=utf8mb4 comment='上报任务';

create table if not exists reg_report_receipt (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  task_id bigint not null comment '关联任务ID',
  success tinyint not null comment '是否成功',
  platform_code varchar(64) null comment '平台返回码',
  platform_message varchar(1024) null comment '平台返回信息',
  receipt_time datetime not null comment '回执时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_reg_report_receipt_task (tenant_id, task_id),
  key idx_reg_report_receipt_time (tenant_id, receipt_time)
) engine=InnoDB default charset=utf8mb4 comment='上报回执';

create table if not exists reg_retry_queue (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  task_id bigint not null comment '关联任务ID',
  retry_count int not null default 0 comment '已重试次数',
  max_retries int not null default 3 comment '最大重试次数',
  next_retry_at datetime null comment '下次重试时间',
  last_error varchar(1024) null comment '最近错误',
  status varchar(32) not null default 'PENDING' comment '队列状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_reg_retry_task (tenant_id, task_id),
  key idx_reg_retry_status (tenant_id, status, next_retry_at)
) engine=InnoDB default charset=utf8mb4 comment='重试队列';
