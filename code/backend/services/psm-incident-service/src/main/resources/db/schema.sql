create table if not exists incident_report (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  incident_no varchar(64) not null comment '事件编号',
  incident_type varchar(32) not null comment '事件类型',
  incident_level varchar(32) not null comment '事件等级',
  occurred_at datetime null comment '发生时间',
  area_id bigint null comment '区域',
  equipment_id bigint null comment '设备',
  source_type varchar(32) null comment '来源类型',
  source_biz_id bigint null comment '来源业务ID',
  status varchar(32) not null default 'REPORTED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_incident_no (tenant_id, incident_no, deleted),
  key idx_incident_report_tenant (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='事件报告';

create table if not exists incident_investigation (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  incident_id bigint not null comment '事件ID',
  lead_user_id bigint null comment '组长',
  scope_desc varchar(2048) null comment '调查范围',
  status varchar(32) not null default 'IN_PROGRESS' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_incident_investigation_incident (tenant_id, incident_id)
) engine=InnoDB default charset=utf8mb4 comment='调查主表';

create table if not exists incident_timeline (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  incident_id bigint not null comment '事件ID',
  event_at datetime not null comment '时间点',
  event_desc varchar(1024) not null comment '描述',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_incident_timeline_incident (tenant_id, incident_id)
) engine=InnoDB default charset=utf8mb4 comment='时间线';

create table if not exists incident_evidence (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  incident_id bigint not null comment '事件ID',
  evidence_type varchar(32) not null comment '证据类型',
  file_id bigint null comment '文件ID',
  description varchar(1024) null comment '描述',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_incident_evidence_incident (tenant_id, incident_id)
) engine=InnoDB default charset=utf8mb4 comment='证据附件';

create table if not exists incident_interview (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  incident_id bigint not null comment '事件ID',
  interviewee varchar(128) not null comment '被访谈人',
  summary varchar(2048) null comment '摘要',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_incident_interview_incident (tenant_id, incident_id)
) engine=InnoDB default charset=utf8mb4 comment='访谈记录';

create table if not exists incident_root_cause (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  incident_id bigint not null comment '事件ID',
  cause_type varchar(32) not null comment '原因类型',
  cause_desc varchar(2048) not null comment '原因描述',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_incident_root_cause_incident (tenant_id, incident_id)
) engine=InnoDB default charset=utf8mb4 comment='根因';

create table if not exists incident_capa (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  incident_id bigint not null comment '事件ID',
  capa_no varchar(64) not null comment 'CAPA编号',
  capa_type varchar(32) not null comment 'CAPA类型',
  owner_user_id bigint null comment '责任人',
  due_at datetime null comment '截止时间',
  verification_user_id bigint null comment '验证人',
  evidence_file_id bigint null comment '证据附件',
  status varchar(32) not null default 'PENDING_ASSIGN' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_incident_capa_incident (tenant_id, incident_id),
  key idx_incident_capa_status (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='CAPA';

create table if not exists lesson_learned (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  incident_id bigint not null comment '事件ID',
  lesson_desc varchar(2048) not null comment '经验描述',
  action_type varchar(32) not null comment '行动类型',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_lesson_learned_incident (tenant_id, incident_id)
) engine=InnoDB default charset=utf8mb4 comment='经验反馈';
