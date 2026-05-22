create table if not exists auth_user (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  username varchar(128) not null,
  display_name varchar(128) null,
  mobile varchar(32) null,
  email varchar(128) null,
  password_hash varchar(512) null,
  account_type varchar(32) not null,
  status varchar(32) not null default 'ENABLED',
  last_login_at datetime null,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  deleted tinyint not null default 0,
  primary key (id),
  unique key uk_auth_user_username (tenant_id, username, deleted),
  key idx_auth_user_mobile (tenant_id, mobile),
  key idx_auth_user_email (tenant_id, email)
) engine=InnoDB default charset=utf8mb4 comment='认证用户';

create table if not exists auth_session (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  user_id bigint not null,
  access_token varchar(128) not null,
  refresh_token varchar(128) not null,
  access_expires_at datetime not null,
  refresh_expires_at datetime not null,
  revoked tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key (id),
  unique key uk_auth_session_access (access_token),
  unique key uk_auth_session_refresh (refresh_token),
  key idx_auth_session_user (tenant_id, user_id)
) engine=InnoDB default charset=utf8mb4 comment='认证会话';

create table if not exists auth_identity_provider (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  provider_code varchar(128) not null,
  provider_type varchar(32) not null,
  provider_name varchar(128) not null,
  client_id varchar(255) null,
  client_secret varchar(512) null,
  authorize_url varchar(512) not null,
  token_url varchar(512) null,
  user_info_url varchar(512) null,
  callback_url varchar(512) null,
  user_mapping_field varchar(128) not null default 'sub',
  config json null,
  enabled tinyint not null default 1,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key (id),
  unique key uk_auth_idp_code (tenant_id, provider_code)
) engine=InnoDB default charset=utf8mb4 comment='SSO身份源配置';

create table if not exists auth_user_identity (
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
  unique key uk_auth_user_identity (tenant_id, provider_code, external_user_id),
  key idx_auth_user_identity_user (tenant_id, user_id)
) engine=InnoDB default charset=utf8mb4 comment='SSO用户映射';

create table if not exists auth_sso_state (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  provider_code varchar(128) not null,
  state varchar(128) not null,
  expires_at datetime not null,
  redirect_after_login varchar(512) null,
  consumed tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  primary key (id),
  unique key uk_auth_sso_state (tenant_id, provider_code, state),
  key idx_auth_sso_state_expire (expires_at)
) engine=InnoDB default charset=utf8mb4 comment='SSO登录状态';

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
