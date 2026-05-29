create table if not exists barrier (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  barrier_code varchar(64) comment '屏障编码',
  barrier_name varchar(255) comment '屏障名称',
  barrier_type varchar(32) comment '屏障类型',
  major_hazard_id bigint comment '重大危险源ID',
  hazop_scenario_id bigint comment '场景ID',
  owner_org_id bigint comment '责任部门',
  health_score decimal(5,2) comment '健康度',
  status varchar(32) not null default 'NORMAL' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_barrier_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='Barrier';

create table if not exists barrier_element (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  barrier_id bigint comment '屏障ID',
  element_name varchar(255) comment '元素名称',
  element_type varchar(32) comment '元素类型',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_barrier_element_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='BarrierElement';

create table if not exists barrier_health_snapshot (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  barrier_id bigint comment '屏障ID',
  health_score decimal(5,2) comment '健康度',
  snapshot_at datetime comment '快照时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_barrier_health_snapshot_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='BarrierHealthSnapshot';

create table if not exists barrier_degradation (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  barrier_id bigint comment '屏障ID',
  from_status varchar(32) comment '原状态',
  to_status varchar(32) comment '目标状态',
  reason varchar(1024) comment '原因',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_barrier_degradation_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='BarrierDegradation';

create table if not exists compensating_measure (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  barrier_id bigint comment '屏障ID',
  measure_desc varchar(1024) comment '补偿措施',
  status varchar(32) not null default 'ACTIVE' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_compensating_measure_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='CompensatingMeasure';

create table if not exists mi_equipment (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  equipment_code varchar(64) comment '设备编码',
  equipment_name varchar(255) comment '设备名称',
  area_id bigint comment '区域',
  critical_flag tinyint default 0 comment '关键设备',
  status varchar(32) not null default 'ACTIVE' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_mi_equipment_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='MiEquipment';

create table if not exists mi_inspection_plan (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  equipment_id bigint comment '设备ID',
  plan_name varchar(255) comment '计划名称',
  cycle_days int comment '周期天数',
  next_due_at datetime comment '下次到期',
  status varchar(32) not null default 'ACTIVE' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_mi_inspection_plan_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='MiInspectionPlan';

create table if not exists mi_inspection_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  plan_id bigint comment '计划ID',
  equipment_id bigint comment '设备ID',
  inspected_at datetime comment '检验时间',
  result varchar(32) comment '结果',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_mi_inspection_record_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='MiInspectionRecord';

create table if not exists mi_defect (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  defect_no varchar(64) comment '缺陷编号',
  equipment_id bigint comment '设备ID',
  defect_level varchar(32) comment '缺陷等级',
  source_type varchar(32) comment '来源类型',
  description varchar(2048) comment '描述',
  repair_deadline datetime comment '维修期限',
  status varchar(32) not null default 'PENDING_CONFIRM' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_mi_defect_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='MiDefect';

create table if not exists mi_maintenance_task (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  defect_id bigint comment '缺陷ID',
  task_desc varchar(1024) comment '任务描述',
  owner_user_id bigint comment '责任人',
  status varchar(32) not null default 'PENDING' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_mi_maintenance_task_tenant (tenant_id)
) engine=InnoDB default charset=utf8mb4 comment='MiMaintenanceTask';
