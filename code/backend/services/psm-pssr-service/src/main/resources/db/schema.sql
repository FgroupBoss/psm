create table if not exists pssr_project (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  pssr_no varchar(64) comment 'PSSR编号',
  project_name varchar(255) comment '项目名称',
  source_type varchar(32) comment '来源类型',
  source_biz_id bigint comment '来源业务ID',
  area_id bigint comment '区域',
  equipment_id bigint comment '设备',
  planned_startup_at datetime comment '计划开车时间',
  approval_status varchar(32) comment '批准状态',
  status varchar(32) not null default 'DRAFT' comment '项目状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pssr_project_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='PssrProject';

create table if not exists pssr_template (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  template_code varchar(64) comment '模板编码',
  template_name varchar(255) comment '模板名称',
  unit_type varchar(32) comment '装置类型',
  status varchar(32) not null default 'ACTIVE' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pssr_template_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='PssrTemplate';

create table if not exists pssr_check_item (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  template_id bigint comment '模板ID',
  item_no varchar(64) comment '检查项编号',
  item_desc varchar(1024) comment '检查项描述',
  discipline varchar(32) comment '专业',
  issue_level varchar(8) comment '问题等级',
  startup_block_flag tinyint default 0 comment '开车前必关',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pssr_check_item_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='PssrCheckItem';

create table if not exists pssr_execution_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  project_id bigint comment '项目ID',
  check_item_id bigint comment '检查项ID',
  result varchar(32) comment '结果',
  remark varchar(1024) comment '备注',
  executor_user_id bigint comment '执行人',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pssr_execution_record_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='PssrExecutionRecord';

create table if not exists pssr_issue (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  project_id bigint comment '项目ID',
  check_item_id bigint comment '检查项ID',
  issue_level varchar(8) comment '问题等级',
  description varchar(2048) comment '问题描述',
  owner_user_id bigint comment '责任人',
  due_at datetime comment '整改期限',
  close_required_before_startup tinyint comment '开车前必关',
  status varchar(32) not null default 'PENDING_RECTIFY' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pssr_issue_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='PssrIssue';

create table if not exists pssr_approval_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  project_id bigint comment '项目ID',
  approver_user_id bigint comment '批准人',
  decision varchar(32) comment '决策',
  comment_text varchar(1024) comment '意见',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pssr_approval_record_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='PssrApprovalRecord';
