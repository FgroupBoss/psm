create table if not exists master_data_item (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  category varchar(64) not null comment '主数据分类',
  item_code varchar(128) not null comment '编码',
  item_name varchar(255) not null comment '名称',
  parent_id bigint null comment '上级ID',
  item_type varchar(64) null comment '类型',
  status varchar(32) not null default 'ENABLED' comment '状态',
  attributes json null comment '扩展属性',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_master_data_item_code (tenant_id, category, item_code, deleted),
  key idx_master_data_item_parent (tenant_id, category, parent_id),
  key idx_master_data_item_status (tenant_id, category, status)
) engine=InnoDB default charset=utf8mb4 comment='基础主数据统一表';

create table if not exists base_area (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  area_code varchar(128) not null comment '区域编码',
  area_name varchar(255) not null comment '区域名称',
  parent_id bigint null comment '上级区域ID',
  area_type varchar(64) null comment '区域类型',
  site_id bigint null comment '厂区ID',
  risk_level varchar(32) null comment '风险等级',
  major_hazard_flag tinyint not null default 0 comment '是否重大危险源区域',
  sort_no int not null default 0 comment '排序号',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_base_area_code (tenant_id, area_code, deleted),
  key idx_base_area_parent (tenant_id, parent_id),
  key idx_base_area_status (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='区域台账';

create table if not exists base_unit (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  unit_code varchar(128) not null comment '装置编码',
  unit_name varchar(255) not null comment '装置名称',
  area_id bigint not null comment '所属区域ID',
  unit_type varchar(64) null comment '装置类型',
  sort_no int not null default 0 comment '排序号',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_base_unit_code (tenant_id, unit_code, deleted),
  key idx_base_unit_area (tenant_id, area_id),
  key idx_base_unit_status (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='装置台账';

create table if not exists base_equipment (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  equipment_code varchar(128) not null comment '设备编码',
  equipment_name varchar(255) not null comment '设备名称',
  area_id bigint not null comment '所属区域ID',
  unit_id bigint null comment '所属装置ID',
  equipment_type varchar(64) null comment '设备类型',
  running_status varchar(32) null comment '运行状态',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_base_equipment_code (tenant_id, equipment_code, deleted),
  key idx_base_equipment_area (tenant_id, area_id),
  key idx_base_equipment_unit (tenant_id, unit_id),
  key idx_base_equipment_status (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='设备台账';

create table if not exists monitor_point (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  point_code varchar(128) not null comment '点位编码',
  point_name varchar(255) not null comment '点位名称',
  area_id bigint not null comment '所属区域ID',
  equipment_id bigint null comment '所属设备ID',
  source_system varchar(64) null comment '来源系统',
  source_tag varchar(128) null comment '来源标签',
  metric_type varchar(64) null comment '指标类型',
  unit varchar(32) null comment '计量单位',
  high_high decimal(18,6) null comment '高高限',
  high decimal(18,6) null comment '高限',
  low decimal(18,6) null comment '低限',
  low_low decimal(18,6) null comment '低低限',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_monitor_point_code (tenant_id, point_code, deleted),
  key idx_monitor_point_area (tenant_id, area_id),
  key idx_monitor_point_equipment (tenant_id, equipment_id),
  key idx_monitor_point_status (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='监测点位台账';

create table if not exists sys_tenant_domain (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  domain varchar(255) not null,
  identity_provider_code varchar(128) null,
  enabled tinyint not null default 1,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key (id),
  unique key uk_tenant_domain (domain)
) engine=InnoDB default charset=utf8mb4 comment='租户域名和登录入口';

create table if not exists sys_user_identity (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  user_id bigint not null,
  provider_code varchar(128) not null,
  external_user_id varchar(255) not null,
  external_username varchar(255) null,
  enabled tinyint not null default 1,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key (id),
  unique key uk_user_identity (tenant_id, provider_code, external_user_id)
) engine=InnoDB default charset=utf8mb4 comment='外部身份与本地用户映射';

create table if not exists audit_change_log (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  operator_id bigint null,
  operator_name varchar(128) null,
  action varchar(64) not null,
  biz_type varchar(64) not null,
  biz_id bigint null,
  before_value json null,
  after_value json null,
  result varchar(32) not null,
  client_ip varchar(64) null,
  user_agent varchar(512) null,
  operated_at datetime not null default current_timestamp,
  primary key (id),
  key idx_audit_change_log_biz (tenant_id, biz_type, biz_id),
  key idx_audit_change_log_time (tenant_id, operated_at)
) engine=InnoDB default charset=utf8mb4 comment='关键数据变更审计';
