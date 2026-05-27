create table if not exists notification_message (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  user_id bigint not null comment '接收用户ID',
  request_id varchar(128) null comment '幂等键',
  channel varchar(32) not null default 'IN_APP' comment '通道',
  template_code varchar(64) null comment '模板编码',
  title varchar(255) not null comment '标题',
  content varchar(2048) not null comment '正文',
  biz_type varchar(64) null comment '业务类型',
  biz_id bigint null comment '业务ID',
  read_flag tinyint not null default 0 comment '0未读1已读',
  read_at datetime null comment '已读时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  unique key uk_notification_request (tenant_id, user_id, request_id),
  key idx_notification_inbox (tenant_id, user_id, read_flag, created_at)
) engine=InnoDB default charset=utf8mb4 comment='站内信收件箱';

create table if not exists notification_delivery_log (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  message_id bigint not null comment '消息ID',
  channel varchar(32) not null comment '通道',
  status varchar(32) not null comment 'PENDING/SENT/FAILED',
  error_message varchar(512) null comment '失败原因',
  sent_at datetime null comment '发送时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_notification_delivery_msg (tenant_id, message_id)
) engine=InnoDB default charset=utf8mb4 comment='通知发送日志';
