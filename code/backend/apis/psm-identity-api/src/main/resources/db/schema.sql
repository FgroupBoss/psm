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
  message_id bigint null comment '消息ID',
  request_id varchar(128) null comment '投递幂等键',
  channel varchar(32) not null comment '通道',
  status varchar(32) not null comment 'PENDING/SENT/FAILED/SKIPPED',
  provider_msg_id varchar(128) null comment '厂商回执ID',
  error_code varchar(64) null comment '错误码',
  error_message varchar(512) null comment '失败原因',
  sent_at datetime null comment '发送时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  primary key (id),
  key idx_notification_delivery_msg (tenant_id, message_id),
  unique key uk_notification_delivery_req (tenant_id, request_id, channel)
) engine=InnoDB default charset=utf8mb4 comment='通知发送日志';

create table if not exists file_object (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  file_name varchar(255) not null comment '原始文件名',
  content_type varchar(128) null comment 'MIME',
  size_bytes bigint not null default 0 comment '字节数',
  sha256 varchar(64) null comment 'SHA-256',
  storage_backend varchar(32) not null default 'LOCAL' comment '存储后端',
  storage_profile_id bigint null comment '配置档ID',
  bucket varchar(128) null comment '桶',
  object_key varchar(512) null comment '对象键',
  region varchar(64) null comment '区域',
  storage_path varchar(512) null comment 'LOCAL相对路径',
  biz_type varchar(64) null comment '业务类型',
  biz_id bigint null comment '业务ID',
  status varchar(32) not null default 'ACTIVE' comment '状态',
  created_by varchar(64) null comment '上传人',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '逻辑删除',
  primary key (id),
  key idx_file_object_tenant_biz (tenant_id, biz_type, biz_id),
  key idx_file_object_tenant_created (tenant_id, created_at)
) engine=InnoDB default charset=utf8mb4 comment='文件元数据';

create table if not exists file_storage_profile (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  profile_code varchar(64) not null comment '配置编码',
  profile_name varchar(128) not null comment '配置名称',
  storage_backend varchar(32) not null comment '存储后端',
  config_json text null comment '连接配置JSON',
  enabled tinyint not null default 1 comment '是否启用',
  is_default tinyint not null default 0 comment '是否默认',
  last_test_status varchar(32) null comment '最近测试状态',
  last_test_at datetime null comment '最近测试时间',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp comment '更新时间',
  primary key (id),
  unique key uk_file_storage_profile (tenant_id, profile_code),
  key idx_file_storage_default (tenant_id, is_default)
) engine=InnoDB default charset=utf8mb4 comment='租户文件存储配置';
