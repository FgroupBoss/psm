create table if not exists operation_log (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  operator_id bigint null,
  operator_name varchar(128) null,
  action varchar(64) not null,
  biz_type varchar(64) not null,
  biz_id bigint null,
  result varchar(32) not null,
  client_ip varchar(64) null,
  user_agent varchar(512) null,
  operated_at datetime not null default current_timestamp,
  primary key (id),
  key idx_operation_log_biz (tenant_id, biz_type, biz_id),
  key idx_operation_log_time (tenant_id, operated_at)
) engine=InnoDB default charset=utf8mb4 comment='操作日志';

create table if not exists login_log (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  user_id bigint null,
  username varchar(128) null,
  login_type varchar(32) not null,
  result varchar(32) not null,
  failure_reason varchar(255) null,
  client_ip varchar(64) null,
  user_agent varchar(512) null,
  logged_at datetime not null default current_timestamp,
  primary key (id),
  key idx_login_log_user (tenant_id, user_id),
  key idx_login_log_time (tenant_id, logged_at)
) engine=InnoDB default charset=utf8mb4 comment='登录日志';

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
