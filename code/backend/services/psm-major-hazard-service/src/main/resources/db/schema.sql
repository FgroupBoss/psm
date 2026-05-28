create table if not exists major_hazard (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  hazard_no varchar(128) not null comment '危险源编码',
  name varchar(255) not null comment '名称',
  hazard_type varchar(64) null comment '类型',
  level varchar(32) not null comment '等级',
  area_id bigint null comment '关联区域ID',
  unit_id bigint null comment '关联装置ID',
  material varchar(255) null comment '主要介质',
  design_capacity varchar(64) null comment '设计容量',
  actual_capacity varchar(64) null comment '实际容量',
  critical_quantity varchar(64) null comment '临界量',
  emergency_plan_id bigint null comment '应急预案ID',
  default_inspection_plan_id bigint null comment '默认巡检计划ID',
  status varchar(32) not null default 'DRAFT' comment '状态',
  published_at datetime null comment '发布时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_major_hazard_no (tenant_id, hazard_no, deleted),
  key idx_major_hazard_area (tenant_id, area_id),
  key idx_major_hazard_status (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='重大危险源主档';

create table if not exists major_hazard_responsibility (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  hazard_id bigint not null comment '危险源ID',
  responsibility_type varchar(32) not null comment '责任类型 PRIMARY/TECHNICAL/OPERATION',
  person_name varchar(128) not null comment '责任人姓名',
  person_phone varchar(64) null comment '联系电话',
  person_id bigint null comment '关联用户ID',
  sort_no int not null default 0 comment '排序',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_major_hazard_resp (tenant_id, hazard_id, responsibility_type),
  key idx_major_hazard_resp_hazard (tenant_id, hazard_id)
) engine=InnoDB default charset=utf8mb4 comment='重大危险源包保责任';

create table if not exists major_hazard_point_rel (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  hazard_id bigint not null comment '危险源ID',
  monitor_point_id bigint not null comment '监测点位ID',
  point_code varchar(128) null comment '点位编码冗余',
  point_name varchar(255) null comment '点位名称冗余',
  created_at datetime not null default current_timestamp comment '创建时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_major_hazard_point (tenant_id, hazard_id, monitor_point_id, deleted),
  key idx_major_hazard_point_hazard (tenant_id, hazard_id)
) engine=InnoDB default charset=utf8mb4 comment='重大危险源与监测点位关联';

create table if not exists major_hazard_attachment (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  hazard_id bigint not null comment '危险源ID',
  attachment_type varchar(64) not null comment '附件分类',
  file_id bigint not null comment '文件中心ID',
  file_name varchar(255) null comment '文件名',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_major_hazard_attachment (tenant_id, hazard_id)
) engine=InnoDB default charset=utf8mb4 comment='重大危险源附件元数据';

create table if not exists major_hazard_status_log (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  hazard_id bigint not null comment '危险源ID',
  from_status varchar(32) null comment '变更前状态',
  to_status varchar(32) not null comment '变更后状态',
  reason varchar(512) null comment '原因',
  operator_id bigint null comment '操作人ID',
  operator_name varchar(128) null comment '操作人姓名',
  operated_at datetime not null default current_timestamp comment '操作时间',
  primary key (id),
  key idx_major_hazard_status_log (tenant_id, hazard_id, operated_at)
) engine=InnoDB default charset=utf8mb4 comment='重大危险源状态变更记录';

create table if not exists major_hazard_audit_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  target_type varchar(32) not null comment '对象类型',
  target_id bigint not null comment '对象ID',
  action varchar(64) not null comment '动作',
  before_status varchar(32) null comment '变更前状态',
  after_status varchar(32) null comment '变更后状态',
  opinion varchar(512) null comment '备注/原因',
  operator_id bigint null comment '操作人ID',
  operator_name varchar(128) null comment '操作人姓名',
  operated_at datetime not null default current_timestamp comment '操作时间',
  primary key (id),
  key idx_major_hazard_audit_target (tenant_id, target_type, target_id),
  key idx_major_hazard_audit_time (tenant_id, operated_at)
) engine=InnoDB default charset=utf8mb4 comment='重大危险源操作审计流水';
