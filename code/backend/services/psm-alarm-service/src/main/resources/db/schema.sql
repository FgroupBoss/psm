create table if not exists alarm_event (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  alarm_no varchar(128) not null comment '报警编号',
  source_type varchar(64) not null comment '来源类型',
  source_code varchar(128) null comment '来源编码',
  title varchar(255) not null comment '标题',
  content varchar(1024) null comment '内容',
  alarm_level varchar(32) not null comment '报警等级',
  status varchar(32) not null default 'NEW' comment '状态',
  area_id bigint null comment '区域ID',
  unit_id bigint null comment '装置ID',
  equipment_id bigint null comment '设备ID',
  monitor_point_id bigint null comment '监测点位ID',
  hazard_id bigint null comment '重大危险源ID',
  dedup_key varchar(256) null comment '去重键',
  occurrence_count int not null default 1 comment '发生次数',
  first_occurred_at datetime not null comment '首次发生时间',
  last_occurred_at datetime not null comment '最近发生时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_alarm_event_no (tenant_id, alarm_no, deleted),
  key idx_alarm_event_status (tenant_id, status, last_occurred_at),
  key idx_alarm_event_area (tenant_id, area_id, alarm_level, status),
  key idx_alarm_event_dedup (tenant_id, dedup_key)
) engine=InnoDB default charset=utf8mb4 comment='报警事件主表';

create table if not exists alarm_occurrence (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  alarm_event_id bigint not null comment '报警事件ID',
  occurred_at datetime not null comment '发生时间',
  raw_value varchar(128) null comment '原始值',
  snapshot_json text null comment '快照JSON',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_alarm_occurrence_event (tenant_id, alarm_event_id, occurred_at)
) engine=InnoDB default charset=utf8mb4 comment='报警发生明细';

create table if not exists alarm_action_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  alarm_event_id bigint not null comment '报警事件ID',
  action_type varchar(32) not null comment '动作类型',
  action_content varchar(1024) null comment '动作内容',
  operator_id bigint null comment '操作人ID',
  operator_name varchar(128) null comment '操作人姓名',
  operated_at datetime not null default current_timestamp comment '操作时间',
  primary key (id),
  key idx_alarm_action_event (tenant_id, alarm_event_id, operated_at)
) engine=InnoDB default charset=utf8mb4 comment='报警处置动作记录';

create table if not exists alarm_escalation_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  alarm_event_id bigint not null comment '报警事件ID',
  escalation_level varchar(32) not null comment '升级等级',
  reason varchar(512) null comment '升级原因',
  operator_name varchar(128) null comment '触发人/系统',
  escalated_at datetime not null default current_timestamp comment '升级时间',
  primary key (id),
  key idx_alarm_escalation_event (tenant_id, alarm_event_id, escalated_at)
) engine=InnoDB default charset=utf8mb4 comment='报警升级记录';

create table if not exists alarm_notification_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  alarm_event_id bigint not null comment '报警事件ID',
  channel varchar(32) not null comment '通知渠道',
  notify_target varchar(255) null comment '通知目标',
  notify_content varchar(1024) null comment '通知内容',
  status varchar(32) not null default 'PENDING' comment '发送状态',
  sent_at datetime null comment '发送时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_alarm_notification_event (tenant_id, alarm_event_id)
) engine=InnoDB default charset=utf8mb4 comment='报警通知记录';

create table if not exists alarm_rule (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  rule_code varchar(64) not null comment '规则编码',
  rule_name varchar(128) not null comment '规则名称',
  rule_type varchar(32) not null comment '规则类型 GRADE/TIMEOUT',
  config_json text null comment '规则配置JSON',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_alarm_rule_code (tenant_id, rule_code)
) engine=InnoDB default charset=utf8mb4 comment='报警规则';

create table if not exists alarm_dedup_rule (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  rule_code varchar(64) not null comment '规则编码',
  source_type varchar(64) not null comment '来源类型',
  window_seconds int not null default 300 comment '去重时间窗秒',
  enabled tinyint not null default 1 comment '是否启用',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_alarm_dedup_rule (tenant_id, rule_code)
) engine=InnoDB default charset=utf8mb4 comment='报警去重规则';
