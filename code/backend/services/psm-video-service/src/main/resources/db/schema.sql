create table if not exists video_camera (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  camera_code varchar(128) not null comment '摄像头编码',
  camera_name varchar(255) not null comment '摄像头名称',
  platform_code varchar(64) not null comment '视频平台编码',
  platform_camera_id varchar(128) null comment '平台侧摄像头ID',
  area_id bigint null comment '区域ID',
  major_hazard_id bigint null comment '危险源ID',
  location_desc varchar(512) null comment '位置描述',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_video_camera_code (tenant_id, camera_code, deleted),
  key idx_video_camera_area (tenant_id, area_id),
  key idx_video_camera_hazard (tenant_id, major_hazard_id)
) engine=InnoDB default charset=utf8mb4 comment='摄像头台账';

create table if not exists video_ai_event (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  event_no varchar(64) not null comment '事件编号',
  source_platform varchar(64) not null comment '来源平台',
  camera_id bigint not null comment '摄像头ID',
  event_type varchar(64) not null comment '事件类型',
  event_time datetime not null comment '发生时间',
  area_id bigint null comment '区域ID',
  major_hazard_id bigint null comment '危险源ID',
  alarm_id bigint null comment '关联报警ID',
  work_permit_id bigint null comment '关联作业票ID',
  severity varchar(32) not null comment '严重程度',
  status varchar(32) not null default 'NEW' comment '状态 NEW/ALARMED/IGNORED',
  title varchar(255) null comment '标题',
  description varchar(1024) null comment '描述',
  ignore_reason varchar(512) null comment '忽略原因',
  dedup_key varchar(128) null comment '去重键',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_video_ai_event_no (tenant_id, event_no),
  key idx_video_ai_event_camera (tenant_id, camera_id, event_time),
  key idx_video_ai_event_status (tenant_id, status, event_time),
  key idx_video_ai_event_dedup (tenant_id, dedup_key, event_time)
) engine=InnoDB default charset=utf8mb4 comment='AI视频事件';

create table if not exists video_ai_event_media (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  event_id bigint not null comment '事件ID',
  media_type varchar(32) not null comment 'SNAPSHOT/CLIP',
  file_id bigint null comment '文件服务ID',
  media_url varchar(1024) null comment '外部媒体地址',
  created_at datetime not null default current_timestamp comment '创建时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_video_ai_event_media_event (tenant_id, event_id)
) engine=InnoDB default charset=utf8mb4 comment='AI事件媒体';

create table if not exists video_watch_session (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  work_permit_id bigint not null comment '作业票ID',
  camera_ids varchar(512) not null comment '监护摄像头ID列表(逗号分隔)',
  operator_id bigint null comment '监护人ID',
  operator_name varchar(128) null comment '监护人姓名',
  status varchar(32) not null default 'ACTIVE' comment 'ACTIVE/CLOSED',
  started_at datetime not null comment '开始时间',
  ended_at datetime null comment '结束时间',
  remark varchar(512) null comment '备注',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_video_watch_permit (tenant_id, work_permit_id, status)
) engine=InnoDB default charset=utf8mb4 comment='作业过程监护会话';

create table if not exists video_watch_capture (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  session_id bigint not null comment '监护会话ID',
  camera_id bigint null comment '摄像头ID',
  capture_type varchar(32) not null comment 'SNAPSHOT/CLIP',
  file_id bigint null comment '文件服务ID',
  media_url varchar(1024) null comment '外部媒体地址',
  captured_at datetime not null comment '采集时间',
  remark varchar(512) null comment '备注',
  created_at datetime not null default current_timestamp comment '创建时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_video_watch_capture_session (tenant_id, session_id, captured_at)
) engine=InnoDB default charset=utf8mb4 comment='监护截图片段';

create table if not exists video_platform_config (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  platform_code varchar(64) not null comment '平台编码',
  platform_name varchar(128) not null comment '平台名称',
  ingest_secret varchar(256) null comment '接入验签密钥',
  live_url_pattern varchar(1024) null comment '直播地址模板',
  playback_url_pattern varchar(1024) null comment '回放地址模板',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_video_platform_code (tenant_id, platform_code, deleted)
) engine=InnoDB default charset=utf8mb4 comment='视频平台接入配置';
