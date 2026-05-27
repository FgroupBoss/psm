create table if not exists report_export_task (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  report_type varchar(64) not null comment '报表类型',
  export_format varchar(32) not null default 'CSV' comment '导出格式',
  query_params_json text null comment '查询参数JSON',
  status varchar(32) not null default 'PENDING' comment '任务状态',
  file_path varchar(512) null comment '导出文件路径',
  error_message varchar(1024) null comment '失败原因',
  requested_by varchar(128) null comment '请求人',
  started_at datetime null comment '开始时间',
  completed_at datetime null comment '完成时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  key idx_report_export_tenant (tenant_id, report_type, status, created_at)
) engine=InnoDB default charset=utf8mb4 comment='报表导出任务';

create table if not exists acceptance_test_case (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  case_code varchar(64) not null comment '用例编码',
  case_name varchar(255) not null comment '用例名称',
  module varchar(64) not null comment '所属模块',
  scenario varchar(1024) null comment '测试场景',
  expected_result varchar(1024) null comment '预期结果',
  priority varchar(16) not null default 'P1' comment '优先级',
  status varchar(32) not null default 'ACTIVE' comment '用例状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_acceptance_case_code (tenant_id, case_code),
  key idx_acceptance_case_module (tenant_id, module, status)
) engine=InnoDB default charset=utf8mb4 comment='UAT验收用例';

create table if not exists acceptance_test_run (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  case_id bigint not null comment '用例ID',
  run_no varchar(64) not null comment '执行批次号',
  executor_name varchar(128) null comment '执行人',
  run_status varchar(32) not null default 'PASS' comment '执行结果',
  evidence_ref varchar(512) null comment '证据引用',
  remark varchar(1024) null comment '备注',
  executed_at datetime null comment '执行时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_acceptance_run_no (tenant_id, run_no),
  key idx_acceptance_run_case (tenant_id, case_id, executed_at)
) engine=InnoDB default charset=utf8mb4 comment='UAT验收执行记录';
