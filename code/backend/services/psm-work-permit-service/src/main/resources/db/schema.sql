create table if not exists work_permit (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  permit_no varchar(64) not null comment '作业票编号',
  work_type varchar(64) not null comment '作业类型',
  status varchar(32) not null default 'DRAFT' comment '状态',
  title varchar(255) null comment '标题',
  work_content varchar(1024) null comment '作业内容',
  area_id bigint null comment '作业区域',
  unit_id bigint null comment '装置',
  equipment_id bigint null comment '设备',
  hazard_id bigint null comment '重大危险源',
  contractor_company_id bigint null comment '承包商单位',
  plan_start_at datetime null comment '计划开始',
  plan_end_at datetime null comment '计划结束',
  actual_start_at datetime null comment '实际开始',
  actual_end_at datetime null comment '实际结束',
  supervisor_user_id bigint null comment '作业负责人',
  permit_issuer_user_id bigint null comment '许可人',
  guardian_user_id bigint null comment '监护人',
  reject_reason varchar(512) null comment '驳回/关闭原因',
  created_by varchar(128) null comment '创建人',
  updated_by varchar(128) null comment '更新人',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_work_permit_no (tenant_id, permit_no, deleted),
  key idx_work_permit_status (tenant_id, status, updated_at),
  key idx_work_permit_area (tenant_id, area_id, status),
  key idx_work_permit_hazard (tenant_id, hazard_id, status)
) engine=InnoDB default charset=utf8mb4 comment='作业票主表';

create table if not exists work_permit_worker (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  worker_type varchar(32) not null comment '人员类型 EMPLOYEE/CONTRACTOR',
  worker_id bigint null comment '人员ID',
  worker_name varchar(128) not null comment '姓名',
  role_code varchar(64) null comment '角色编码',
  company_id bigint null comment '承包商单位',
  created_at datetime not null default current_timestamp comment '创建时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_wpw_permit (tenant_id, work_permit_id, deleted)
) engine=InnoDB default charset=utf8mb4 comment='作业票人员';

create table if not exists permit_risk_analysis (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  hazard_desc varchar(512) null comment '危害描述',
  control_measure varchar(1024) null comment '控制措施',
  risk_level varchar(32) null comment '风险等级',
  analyst_name varchar(128) null comment '分析人',
  analyzed_at datetime null comment '分析时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_pra_permit (tenant_id, work_permit_id, deleted)
) engine=InnoDB default charset=utf8mb4 comment='风险分析';

create table if not exists permit_safety_measure (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  measure_code varchar(64) not null comment '措施编码',
  measure_name varchar(255) not null comment '措施名称',
  required_flag tinyint not null default 1 comment '是否必需',
  confirm_status varchar(32) not null default 'PENDING' comment '确认状态',
  confirm_by varchar(128) null comment '确认人',
  confirm_at datetime null comment '确认时间',
  remark varchar(512) null comment '备注',
  attachment_ref varchar(255) null comment '附件引用',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_psm_permit (tenant_id, work_permit_id, deleted)
) engine=InnoDB default charset=utf8mb4 comment='安全措施';

create table if not exists permit_approval_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  action varchar(32) not null comment '动作 APPROVE/RETURN/REJECT',
  opinion varchar(1024) null comment '意见',
  operator_name varchar(128) null comment '操作人',
  operated_at datetime not null default current_timestamp comment '操作时间',
  primary key (id),
  key idx_par_permit (tenant_id, work_permit_id, operated_at)
) engine=InnoDB default charset=utf8mb4 comment='审批记录';

create table if not exists gas_test_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  gas_name varchar(64) not null comment '气体名称',
  measured_value varchar(64) null comment '检测值',
  unit varchar(32) null comment '单位',
  qualified tinyint not null default 0 comment '是否合格',
  tested_at datetime not null comment '检测时间',
  tester_name varchar(128) null comment '检测人',
  remark varchar(512) null comment '备注',
  created_at datetime not null default current_timestamp comment '创建时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_gas_permit (tenant_id, work_permit_id, tested_at)
) engine=InnoDB default charset=utf8mb4 comment='气体检测';

create table if not exists permit_site_confirm (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  confirm_type varchar(32) not null comment '确认类型 CHECK_IN/SIGNATURE',
  confirm_content varchar(1024) null comment '内容',
  location_text varchar(255) null comment '位置',
  scan_code varchar(128) null comment '扫码',
  terminal_id varchar(128) null comment '终端',
  operator_name varchar(128) null comment '操作人',
  confirmed_at datetime not null default current_timestamp comment '确认时间',
  primary key (id),
  key idx_psc_permit (tenant_id, work_permit_id, confirmed_at)
) engine=InnoDB default charset=utf8mb4 comment='现场确认';

create table if not exists permit_monitor_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  record_type varchar(32) not null comment '记录类型',
  content varchar(1024) null comment '内容',
  abnormal_flag tinyint not null default 0 comment '是否异常',
  attachment_ref varchar(255) null comment '附件',
  operator_name varchar(128) null comment '操作人',
  recorded_at datetime not null default current_timestamp comment '记录时间',
  primary key (id),
  key idx_pmr_permit (tenant_id, work_permit_id, recorded_at)
) engine=InnoDB default charset=utf8mb4 comment='监护记录';

create table if not exists permit_acceptance_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  acceptance_result varchar(32) not null comment '验收结果',
  opinion varchar(1024) null comment '意见',
  signature_text varchar(512) null comment '签名文本',
  operator_name varchar(128) null comment '操作人',
  accepted_at datetime not null default current_timestamp comment '验收时间',
  primary key (id),
  key idx_pac_permit (tenant_id, work_permit_id, accepted_at)
) engine=InnoDB default charset=utf8mb4 comment='验收记录';

create table if not exists permit_status_log (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  from_status varchar(32) null comment '原状态',
  to_status varchar(32) not null comment '目标状态',
  action varchar(64) not null comment '动作',
  remark varchar(512) null comment '备注',
  operator_name varchar(128) null comment '操作人',
  operated_at datetime not null default current_timestamp comment '操作时间',
  primary key (id),
  key idx_psl_permit (tenant_id, work_permit_id, operated_at)
) engine=InnoDB default charset=utf8mb4 comment='状态流转日志';

create table if not exists permit_archive_snapshot (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  snapshot_json mediumtext not null comment '归档快照JSON',
  archived_at datetime not null default current_timestamp comment '归档时间',
  archived_by varchar(128) null comment '归档人',
  primary key (id),
  key idx_pas_permit (tenant_id, work_permit_id)
) engine=InnoDB default charset=utf8mb4 comment='归档快照';

create table if not exists mobile_draft_sync (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  client_draft_id varchar(128) not null comment '客户端草稿ID',
  user_id bigint null comment '用户ID',
  draft_type varchar(64) not null comment '草稿类型',
  biz_id bigint null comment '业务ID',
  payload_json mediumtext not null comment '草稿内容',
  sync_status varchar(32) not null default 'PENDING' comment '同步状态',
  last_error varchar(512) null comment '最近错误',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_mobile_draft (tenant_id, client_draft_id)
) engine=InnoDB default charset=utf8mb4 comment='移动弱网草稿';

create table if not exists simops_conflict_rule (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null default 0 comment '租户ID，0表示全局默认',
  work_type_a varchar(64) not null comment '作业类型A',
  work_type_b varchar(64) not null comment '作业类型B',
  area_scope varchar(64) not null default 'ALL' comment '区域范围 ALL或区域ID',
  overlap_minutes int not null default 0 comment '最小重叠分钟数',
  action varchar(32) not null comment 'WARN/COORDINATE/BLOCK',
  enabled tinyint not null default 1 comment '是否启用',
  remark varchar(512) null comment '说明',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_simops_rule_tenant (tenant_id, enabled, deleted),
  key idx_simops_rule_types (work_type_a, work_type_b)
) engine=InnoDB default charset=utf8mb4 comment='SIMOPS冲突矩阵规则';

create table if not exists simops_scan_result (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '当前作业票',
  scan_stage varchar(32) not null comment 'SUBMIT/APPROVE/PERMIT',
  conflict_count int not null default 0 comment '冲突数量',
  max_severity varchar(32) null comment '最高严重级别 WARN/COORDINATE/BLOCK',
  final_action varchar(32) not null default 'PASS' comment 'PASS/WARN/COORDINATE/BLOCK',
  passed tinyint not null default 1 comment '是否通过',
  suggestion varchar(512) null comment '处置建议',
  scanned_at datetime not null default current_timestamp comment '扫描时间',
  primary key (id),
  key idx_simops_scan_permit (tenant_id, work_permit_id, scanned_at),
  key idx_simops_scan_stage (tenant_id, scan_stage, scanned_at)
) engine=InnoDB default charset=utf8mb4 comment='SIMOPS扫描结果';

create table if not exists simops_conflict_item (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  scan_result_id bigint not null comment '扫描结果ID',
  work_permit_id bigint not null comment '当前作业票',
  related_work_permit_id bigint not null comment '冲突作业票',
  rule_id bigint null comment '命中规则ID',
  work_type_a varchar(64) not null comment '作业类型A',
  work_type_b varchar(64) not null comment '作业类型B',
  action varchar(32) not null comment 'WARN/COORDINATE/BLOCK',
  overlap_minutes int not null default 0 comment '重叠分钟数',
  message varchar(512) not null comment '冲突描述',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_simops_item_scan (tenant_id, scan_result_id),
  key idx_simops_item_related (related_work_permit_id)
) engine=InnoDB default charset=utf8mb4 comment='SIMOPS冲突明细';

create table if not exists simops_coordination_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  scan_result_id bigint not null comment '扫描结果ID',
  work_permit_id bigint not null comment '作业票ID',
  decision varchar(32) not null comment 'APPROVED/REJECTED',
  opinion varchar(1024) null comment '协调意见',
  conditions_text varchar(1024) null comment '条件许可说明',
  coordinator_name varchar(128) null comment '协调人',
  coordinated_at datetime not null default current_timestamp comment '协调时间',
  primary key (id),
  key idx_simops_coord_scan (tenant_id, scan_result_id),
  key idx_simops_coord_permit (tenant_id, work_permit_id, coordinated_at)
) engine=InnoDB default charset=utf8mb4 comment='SIMOPS协调审批记录';

insert into simops_conflict_rule (tenant_id, work_type_a, work_type_b, area_scope, overlap_minutes, action, enabled, remark)
select 0, 'HOT_WORK', 'CONFINED_SPACE', 'ALL', 0, 'BLOCK', 1, '动火与受限空间经典SIMOPS硬阻断'
where not exists (select 1 from simops_conflict_rule where tenant_id = 0 and work_type_a = 'HOT_WORK' and work_type_b = 'CONFINED_SPACE' and deleted = 0);

insert into simops_conflict_rule (tenant_id, work_type_a, work_type_b, area_scope, overlap_minutes, action, enabled, remark)
select 0, 'HOT_WORK', 'LIFTING', 'ALL', 0, 'COORDINATE', 1, '动火与吊装需安全协调'
where not exists (select 1 from simops_conflict_rule where tenant_id = 0 and work_type_a = 'HOT_WORK' and work_type_b = 'LIFTING' and deleted = 0);

insert into simops_conflict_rule (tenant_id, work_type_a, work_type_b, area_scope, overlap_minutes, action, enabled, remark)
select 0, 'HOT_WORK', 'TEMPORARY_ELECTRIC', 'ALL', 0, 'WARN', 1, '动火与临时用电检查隔离防火'
where not exists (select 1 from simops_conflict_rule where tenant_id = 0 and work_type_a = 'HOT_WORK' and work_type_b = 'TEMPORARY_ELECTRIC' and deleted = 0);

insert into simops_conflict_rule (tenant_id, work_type_a, work_type_b, area_scope, overlap_minutes, action, enabled, remark)
select 0, 'CONFINED_SPACE', 'BLIND_PLATE', 'ALL', 0, 'COORDINATE', 1, '受限空间与盲板抽堵需确认隔离'
where not exists (select 1 from simops_conflict_rule where tenant_id = 0 and work_type_a = 'CONFINED_SPACE' and work_type_b = 'BLIND_PLATE' and deleted = 0);

insert into simops_conflict_rule (tenant_id, work_type_a, work_type_b, area_scope, overlap_minutes, action, enabled, remark)
select 0, 'EXCAVATION', 'UNDERGROUND_PIPE', 'ALL', 0, 'BLOCK', 1, '动土与地下管线检修防止误挖'
where not exists (select 1 from simops_conflict_rule where tenant_id = 0 and work_type_a = 'EXCAVATION' and work_type_b = 'UNDERGROUND_PIPE' and deleted = 0);

insert into simops_conflict_rule (tenant_id, work_type_a, work_type_b, area_scope, overlap_minutes, action, enabled, remark)
select 0, 'ROAD_BREAK', 'HOT_WORK', 'ALL', 0, 'WARN', 1, '断路影响区域作业'
where not exists (select 1 from simops_conflict_rule where tenant_id = 0 and work_type_a = 'ROAD_BREAK' and work_type_b = 'HOT_WORK' and deleted = 0);

insert into simops_conflict_rule (tenant_id, work_type_a, work_type_b, area_scope, overlap_minutes, action, enabled, remark)
select 0, 'ROAD_BREAK', 'CONFINED_SPACE', 'ALL', 0, 'WARN', 1, '断路影响区域作业'
where not exists (select 1 from simops_conflict_rule where tenant_id = 0 and work_type_a = 'ROAD_BREAK' and work_type_b = 'CONFINED_SPACE' and deleted = 0);
