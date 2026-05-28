create table if not exists insp_checklist_template (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  template_code varchar(64) not null comment '模板编码',
  template_name varchar(255) not null comment '模板名称',
  category varchar(64) null comment '分类',
  status varchar(32) not null default 'ENABLED' comment '状态',
  remark varchar(512) null comment '备注',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_insp_template_code (tenant_id, template_code, deleted),
  key idx_insp_template_tenant (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='巡检检查表模板';

create table if not exists insp_checklist_item (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  template_id bigint not null comment '模板ID',
  item_code varchar(64) not null comment '检查项编码',
  item_name varchar(255) not null comment '检查项名称',
  item_type varchar(32) null comment '项类型 NUMBER/TEXT/OPTION',
  standard_value varchar(128) null comment '标准值',
  lower_limit decimal(18,4) null comment '下限',
  upper_limit decimal(18,4) null comment '上限',
  unit varchar(32) null comment '单位',
  abnormal_rule varchar(512) null comment '异常判定规则',
  photo_required tinyint not null default 0 comment '异常是否需拍照',
  sort_order int not null default 0 comment '排序',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_insp_item_template (tenant_id, template_id, deleted)
) engine=InnoDB default charset=utf8mb4 comment='巡检检查项';

create table if not exists insp_route (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  route_code varchar(64) not null comment '路线编码',
  route_name varchar(255) not null comment '路线名称',
  area_id bigint null comment '区域ID',
  estimated_minutes int null comment '预计时长(分钟)',
  status varchar(32) not null default 'ENABLED' comment '状态',
  remark varchar(512) null comment '备注',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_insp_route_code (tenant_id, route_code, deleted),
  key idx_insp_route_tenant (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='巡检路线';

create table if not exists insp_route_point (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  route_id bigint not null comment '路线ID',
  point_code varchar(64) not null comment '点位编码',
  point_name varchar(255) not null comment '点位名称',
  sign_type varchar(32) not null comment '签到类型 NFC/QR/AREA',
  sign_code varchar(128) null comment 'NFC或二维码标识',
  area_id bigint null comment '区域ID',
  checklist_template_id bigint null comment '关联检查表模板',
  sort_order int not null default 0 comment '顺序',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_insp_route_point_route (tenant_id, route_id, deleted)
) engine=InnoDB default charset=utf8mb4 comment='巡检路线点位';

create table if not exists insp_plan (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  plan_code varchar(64) not null comment '计划编码',
  plan_name varchar(255) not null comment '计划名称',
  route_id bigint not null comment '路线ID',
  cycle_type varchar(32) not null comment '周期 DAILY/WEEKLY/MONTHLY/CUSTOM',
  cron_expr varchar(128) null comment '自定义周期表达式',
  team_id bigint null comment '责任班组ID',
  team_name varchar(128) null comment '责任班组名称',
  default_executor_id bigint null comment '默认执行人',
  major_hazard_id bigint null comment '关联重大危险源',
  enabled tinyint not null default 1 comment '是否启用',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_insp_plan_code (tenant_id, plan_code, deleted),
  key idx_insp_plan_route (tenant_id, route_id)
) engine=InnoDB default charset=utf8mb4 comment='巡检计划';

create table if not exists insp_task (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  task_no varchar(64) not null comment '任务编号',
  plan_id bigint null comment '来源计划',
  route_id bigint not null comment '巡检路线',
  scheduled_start datetime not null comment '计划开始',
  scheduled_end datetime not null comment '计划结束',
  actual_start datetime null comment '实际开始',
  actual_end datetime null comment '实际结束',
  executor_id bigint null comment '执行人',
  status varchar(32) not null default 'PENDING' comment 'PENDING/IN_PROGRESS/COMPLETED/MISSED',
  completion_rate decimal(5,2) not null default 0 comment '完成率',
  abnormal_count int not null default 0 comment '异常项数',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_insp_task_no (tenant_id, task_no, deleted),
  key idx_insp_task_status (tenant_id, status, scheduled_end),
  key idx_insp_task_executor (tenant_id, executor_id, status)
) engine=InnoDB default charset=utf8mb4 comment='巡检任务';

create table if not exists insp_task_item (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  task_id bigint not null comment '任务ID',
  checklist_item_id bigint not null comment '检查项ID',
  route_point_id bigint null comment '所属点位',
  result_value varchar(255) null comment '实测值',
  result_status varchar(32) null comment 'NORMAL/ABNORMAL/SKIPPED',
  photo_urls varchar(2048) null comment '照片URL逗号分隔',
  remark varchar(512) null comment '备注',
  checked_at datetime null comment '检查时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_insp_task_item (tenant_id, task_id, checklist_item_id),
  key idx_insp_task_item_task (tenant_id, task_id)
) engine=InnoDB default charset=utf8mb4 comment='巡检任务检查项结果';

create table if not exists insp_sign_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  task_id bigint not null comment '任务ID',
  route_point_id bigint not null comment '点位ID',
  sign_type varchar(32) not null comment '签到类型',
  sign_code varchar(128) null comment '签到码',
  signed_at datetime not null comment '签到时间',
  operator_id bigint null comment '操作人ID',
  operator_name varchar(128) null comment '操作人姓名',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_insp_sign_task (tenant_id, task_id, route_point_id)
) engine=InnoDB default charset=utf8mb4 comment='巡检点位签到';

create table if not exists insp_abnormal_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  task_id bigint not null comment '任务ID',
  task_item_id bigint null comment '任务检查项ID',
  route_point_id bigint null comment '点位ID',
  abnormal_desc varchar(1024) not null comment '异常描述',
  photo_urls varchar(2048) not null comment '异常照片',
  severity varchar(32) null comment '严重程度',
  handle_status varchar(32) not null default 'OPEN' comment '处理状态',
  hazard_draft_id bigint null comment '隐患草稿ID',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  key idx_insp_abnormal_task (tenant_id, task_id)
) engine=InnoDB default charset=utf8mb4 comment='巡检异常记录';

create table if not exists insp_task_draft (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  client_draft_id varchar(128) not null comment '客户端草稿ID',
  task_id bigint null comment '关联任务ID',
  payload_json mediumtext not null comment '草稿内容',
  sync_status varchar(32) not null default 'PENDING' comment '同步状态',
  last_error varchar(512) null comment '最近错误',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_insp_task_draft (tenant_id, client_draft_id)
) engine=InnoDB default charset=utf8mb4 comment='巡检任务离线草稿';
