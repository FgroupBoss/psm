create table if not exists master_data_item (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  category varchar(64) not null comment '主数据分类',
  item_code varchar(128) not null comment '编码',
  item_name varchar(255) not null comment '名称',
  parent_id bigint null comment '上级ID',
  item_type varchar(64) null comment '类型',
  status varchar(32) not null default 'ENABLED' comment '状态',
  attributes json null comment '扩展属性',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_master_data_item_code (tenant_id, category, item_code, deleted),
  key idx_master_data_item_parent (tenant_id, category, parent_id),
  key idx_master_data_item_status (tenant_id, category, status)
) engine=InnoDB default charset=utf8mb4 comment='基础主数据统一表';

create table if not exists sys_tenant_domain (
  id bigint not null auto_increment,
  tenant_id bigint not null,
  domain varchar(255) not null,
  identity_provider_code varchar(128) null,
  enabled tinyint not null default 1,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key (id),
  unique key uk_tenant_domain (domain)
) engine=InnoDB default charset=utf8mb4 comment='租户域名和登录入口';

create table if not exists sys_user_identity (
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
  unique key uk_user_identity (tenant_id, provider_code, external_user_id)
) engine=InnoDB default charset=utf8mb4 comment='外部身份与本地用户映射';

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
