create table if not exists sys_tenant (
  id bigint not null auto_increment,
  tenant_code varchar(64) not null,
  tenant_name varchar(128) not null,
  tenant_type varchar(32) not null default 'PILOT',
  status varchar(32) not null default 'ENABLED',
  admin_user_id bigint null,
  data_isolation_mode varchar(32) not null default 'TENANT',
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  deleted tinyint not null default 0,
  primary key (id),
  unique key uk_sys_tenant_code (tenant_code)
) engine=InnoDB default charset=utf8mb4 comment='租户';

create table if not exists sys_org (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  parent_id bigint null,
  org_code varchar(64) not null,
  org_name varchar(128) not null,
  org_type varchar(32) not null,
  sort_order int not null default 0,
  status varchar(32) not null default 'ENABLED',
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  deleted tinyint not null default 0,
  primary key (id),
  unique key uk_sys_org_code (tenant_id, org_code)
) engine=InnoDB default charset=utf8mb4 comment='组织';

create table if not exists sys_post (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  post_code varchar(64) not null,
  post_name varchar(128) not null,
  org_id bigint null,
  status varchar(32) not null default 'ENABLED',
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  deleted tinyint not null default 0,
  primary key (id),
  unique key uk_sys_post_code (tenant_id, post_code)
) engine=InnoDB default charset=utf8mb4 comment='岗位';

create table if not exists sys_user (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  auth_user_id bigint null,
  username varchar(64) not null,
  display_name varchar(128) not null,
  mobile varchar(32) null,
  email varchar(128) null,
  org_id bigint null,
  post_id bigint null,
  account_type varchar(32) not null default 'LOCAL',
  status varchar(32) not null default 'ENABLED',
  permission_version bigint not null default 1,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  deleted tinyint not null default 0,
  primary key (id),
  unique key uk_sys_user_username (tenant_id, username),
  unique key uk_sys_user_auth_user (tenant_id, auth_user_id)
) engine=InnoDB default charset=utf8mb4 comment='IAM用户资料';

create table if not exists sys_role (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  role_code varchar(64) not null,
  role_name varchar(128) not null,
  role_type varchar(32) not null default 'BUSINESS',
  description varchar(512) null,
  status varchar(32) not null default 'ENABLED',
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  deleted tinyint not null default 0,
  primary key (id),
  unique key uk_sys_role_code (tenant_id, role_code)
) engine=InnoDB default charset=utf8mb4 comment='角色';

create table if not exists sys_user_role (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  user_id bigint not null,
  role_id bigint not null,
  created_at datetime not null default current_timestamp,
  primary key (id),
  unique key uk_sys_user_role (tenant_id, user_id, role_id)
) engine=InnoDB default charset=utf8mb4 comment='用户角色关系';

create table if not exists sys_menu (
  id bigint not null auto_increment,
  tenant_id bigint not null default 0,
  parent_id bigint null,
  resource_type varchar(32) not null,
  resource_code varchar(128) not null,
  resource_name varchar(128) not null,
  route_path varchar(255) null,
  api_path varchar(255) null,
  http_method varchar(16) null,
  sort_order int not null default 0,
  visible tinyint not null default 1,
  status varchar(32) not null default 'ENABLED',
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  deleted tinyint not null default 0,
  primary key (id),
  unique key uk_sys_menu_code (tenant_id, resource_code)
) engine=InnoDB default charset=utf8mb4 comment='菜单与权限资源';

create table if not exists sys_role_permission (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  role_id bigint not null,
  resource_id bigint not null,
  permission_code varchar(128) not null,
  created_at datetime not null default current_timestamp,
  primary key (id),
  unique key uk_role_permission (tenant_id, role_id, resource_id)
) engine=InnoDB default charset=utf8mb4 comment='角色功能权限';

create table if not exists sys_data_scope (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  role_id bigint not null,
  scope_type varchar(32) not null,
  org_ids json null,
  area_ids json null,
  unit_ids json null,
  include_children tinyint not null default 1,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key (id),
  unique key uk_role_data_scope (tenant_id, role_id)
) engine=InnoDB default charset=utf8mb4 comment='角色数据权限';

create table if not exists sys_permission_version (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  user_id bigint null,
  version_no bigint not null default 1,
  changed_reason varchar(255) null,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key (id),
  unique key uk_permission_version_user (tenant_id, user_id)
) engine=InnoDB default charset=utf8mb4 comment='权限版本';

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
  key idx_audit_biz (tenant_id, biz_type, biz_id),
  key idx_audit_change_log_time (tenant_id, operated_at)
) engine=InnoDB default charset=utf8mb4 comment='关键数据变更审计';
