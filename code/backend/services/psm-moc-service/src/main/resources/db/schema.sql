create table if not exists moc_change (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  change_no varchar(64) not null comment '变更编号',
  change_title varchar(255) not null comment '变更标题',
  change_type varchar(32) null comment '变更类型',
  change_level varchar(32) null comment '变更等级',
  temporary_flag tinyint not null default 0 comment '临时变更',
  emergency_flag tinyint not null default 0 comment '紧急变更',
  affected_area_id bigint null comment '影响区域',
  affected_equipment_id bigint null comment '影响设备',
  risk_level varchar(32) null comment '风险等级',
  status varchar(32) not null default 'DRAFT' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_moc_change_tenant (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='变更主表';

create table if not exists moc_impact_analysis (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  change_id bigint not null comment '变更ID',
  discipline varchar(32) null comment '专业',
  impact_desc varchar(2048) null comment '影响描述',
  risk_level varchar(32) null comment '风险等级',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_moc_impact_change (tenant_id, change_id)
) engine=InnoDB default charset=utf8mb4 comment='影响分析';

create table if not exists moc_approval_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  change_id bigint not null comment '变更ID',
  approver_user_id bigint null comment '审批人',
  decision varchar(32) null comment '决策',
  comment_text varchar(1024) null comment '意见',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_moc_approval_change (tenant_id, change_id)
) engine=InnoDB default charset=utf8mb4 comment='审批记录';

create table if not exists moc_implementation_task (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  change_id bigint not null comment '变更ID',
  task_desc varchar(1024) null comment '任务描述',
  owner_user_id bigint null comment '责任人',
  planned_at datetime null comment '计划时间',
  status varchar(32) not null default 'PENDING' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_moc_task_change (tenant_id, change_id)
) engine=InnoDB default charset=utf8mb4 comment='实施任务';

create table if not exists moc_close_condition (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  change_id bigint not null comment '变更ID',
  condition_type varchar(32) null comment '条件类型',
  required_flag tinyint not null default 0 comment '是否必做',
  owner_user_id bigint null comment '责任人',
  due_at datetime null comment '截止时间',
  completed_at datetime null comment '完成时间',
  evidence_file_id bigint null comment '证据附件',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_moc_close_change (tenant_id, change_id)
) engine=InnoDB default charset=utf8mb4 comment='关闭条件';

create table if not exists moc_document_update (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  change_id bigint not null comment '变更ID',
  doc_type varchar(32) null comment '文件类型',
  doc_name varchar(255) null comment '文件名称',
  status varchar(32) null comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_moc_doc_change (tenant_id, change_id)
) engine=InnoDB default charset=utf8mb4 comment='文件更新';

create table if not exists moc_training_requirement (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  change_id bigint not null comment '变更ID',
  training_desc varchar(1024) null comment '培训描述',
  owner_user_id bigint null comment '责任人',
  status varchar(32) not null default 'PENDING' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_moc_training_change (tenant_id, change_id)
) engine=InnoDB default charset=utf8mb4 comment='培训需求';
