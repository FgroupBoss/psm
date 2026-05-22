create table if not exists sys_role_permission (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  role_id bigint not null,
  permission_code varchar(128) not null,
  permission_type varchar(32) not null,
  created_at datetime not null default current_timestamp,
  primary key (id),
  unique key uk_role_permission (tenant_id, role_id, permission_code)
) engine=InnoDB default charset=utf8mb4 comment='角色功能权限';

create table if not exists sys_role_data_scope (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  role_id bigint not null,
  data_scope_code varchar(128) not null,
  scope_value json null,
  created_at datetime not null default current_timestamp,
  primary key (id),
  unique key uk_role_data_scope (tenant_id, role_id, data_scope_code)
) engine=InnoDB default charset=utf8mb4 comment='角色数据权限';

create table if not exists sys_api_permission (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  permission_code varchar(128) not null,
  http_method varchar(16) not null,
  api_path varchar(255) not null,
  enabled tinyint not null default 1,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key (id),
  unique key uk_api_permission (tenant_id, permission_code)
) engine=InnoDB default charset=utf8mb4 comment='接口权限资源';
