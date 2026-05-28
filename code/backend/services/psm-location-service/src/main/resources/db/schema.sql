create table if not exists loc_tag (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  tag_no varchar(64) not null comment '标签编号',
  tag_type varchar(32) not null default 'LOCATION' comment '标签类型 LOCATION/GATE',
  vendor_code varchar(64) null comment '厂商编码',
  status varchar(32) not null default 'ENABLED' comment '状态',
  remark varchar(512) null comment '备注',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_loc_tag_no (tenant_id, tag_no, deleted),
  key idx_loc_tag_status (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='定位标签';

create table if not exists loc_tag_binding (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  tag_no varchar(64) not null comment '标签编号',
  person_type varchar(32) not null comment '人员类型 INTERNAL/CONTRACTOR',
  person_id bigint not null comment '人员ID',
  contractor_id bigint null comment '承包商ID',
  bind_at datetime not null comment '绑定时间',
  unbind_at datetime null comment '解绑时间',
  status varchar(32) not null default 'ACTIVE' comment 'ACTIVE/UNBOUND',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_loc_binding_tag (tenant_id, tag_no, status),
  key idx_loc_binding_person (tenant_id, person_type, person_id)
) engine=InnoDB default charset=utf8mb4 comment='标签绑定记录';

create table if not exists loc_realtime (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  tag_no varchar(64) not null comment '标签编号',
  person_id bigint null comment '人员ID',
  area_id bigint null comment '区域ID',
  latitude decimal(12,8) null comment '纬度',
  longitude decimal(12,8) null comment '经度',
  online_status varchar(16) not null default 'ONLINE' comment '在线状态',
  last_seen_at datetime not null comment '最后上报时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_loc_realtime_tag (tenant_id, tag_no),
  key idx_loc_realtime_area (tenant_id, area_id, online_status)
) engine=InnoDB default charset=utf8mb4 comment='实时位置快照';

create table if not exists loc_track_point (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  tag_no varchar(64) not null comment '标签编号',
  person_id bigint null comment '人员ID',
  area_id bigint null comment '区域ID',
  latitude decimal(12,8) null comment '纬度',
  longitude decimal(12,8) null comment '经度',
  recorded_at datetime not null comment '记录时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_loc_track_tag_time (tenant_id, tag_no, recorded_at),
  key idx_loc_track_person_time (tenant_id, person_id, recorded_at)
) engine=InnoDB default charset=utf8mb4 comment='历史轨迹点';

create table if not exists loc_geofence (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  fence_code varchar(64) not null comment '围栏编码',
  fence_name varchar(255) not null comment '围栏名称',
  area_id bigint null comment '关联区域ID',
  fence_type varchar(32) not null comment '围栏类型 FORBIDDEN/OVERCAPACITY/OVERSTAY',
  geometry_json text null comment '几何JSON',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_loc_geofence_code (tenant_id, fence_code, deleted),
  key idx_loc_geofence_area (tenant_id, area_id)
) engine=InnoDB default charset=utf8mb4 comment='电子围栏';

create table if not exists loc_geofence_rule (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  fence_id bigint not null comment '围栏ID',
  rule_type varchar(32) not null comment '规则类型',
  threshold_value int null comment '阈值',
  enabled tinyint not null default 1 comment '是否启用',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_loc_geofence_rule_fence (tenant_id, fence_id)
) engine=InnoDB default charset=utf8mb4 comment='围栏规则';

create table if not exists loc_event (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  event_type varchar(32) not null comment '事件类型',
  tag_no varchar(64) null comment '标签编号',
  person_id bigint null comment '人员ID',
  area_id bigint null comment '区域ID',
  fence_id bigint null comment '围栏ID',
  event_time datetime not null comment '事件时间',
  alarm_id bigint null comment '关联报警ID',
  work_permit_id bigint null comment '关联作业票ID',
  raw_payload varchar(2048) null comment '原始载荷',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_loc_event_tenant_time (tenant_id, event_time),
  key idx_loc_event_type (tenant_id, event_type, event_time)
) engine=InnoDB default charset=utf8mb4 comment='定位事件';

create table if not exists gate_access_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  gate_code varchar(64) not null comment '门禁点编码',
  card_no varchar(64) null comment '门禁卡号',
  tag_no varchar(64) null comment '定位标签',
  person_id bigint null comment '人员ID',
  direction varchar(16) not null comment '进出方向 IN/OUT',
  access_time datetime not null comment '通行时间',
  access_result varchar(32) null comment '通行结果',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_gate_access_time (tenant_id, access_time),
  key idx_gate_access_person (tenant_id, person_id, access_time)
) engine=InnoDB default charset=utf8mb4 comment='门禁进出记录';

create table if not exists vehicle_access_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  plate_no varchar(32) not null comment '车牌号',
  vehicle_type varchar(32) null comment '车辆类型',
  gate_code varchar(64) null comment '门岗编码',
  direction varchar(16) not null comment '进出方向 IN/OUT',
  access_time datetime not null comment '通行时间',
  driver_name varchar(128) null comment '驾驶员',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_vehicle_access_time (tenant_id, access_time),
  key idx_vehicle_access_plate (tenant_id, plate_no, access_time)
) engine=InnoDB default charset=utf8mb4 comment='车辆进出记录';

create table if not exists visitor_access_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  visitor_name varchar(128) not null comment '访客姓名',
  id_card_no varchar(32) null comment '证件号',
  company_name varchar(255) null comment '来访单位',
  host_person_id bigint null comment '接待人ID',
  gate_code varchar(64) null comment '门岗编码',
  tag_no varchar(64) null comment '临时标签',
  visit_purpose varchar(512) null comment '来访事由',
  direction varchar(16) not null comment '进出方向 IN/OUT',
  access_time datetime not null comment '通行时间',
  access_result varchar(32) null comment '通行结果',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_visitor_access_time (tenant_id, access_time),
  key idx_visitor_access_name (tenant_id, visitor_name, access_time)
) engine=InnoDB default charset=utf8mb4 comment='访客进出记录';
