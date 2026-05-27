create table if not exists file_object (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  file_name varchar(255) not null comment '原始文件名',
  content_type varchar(128) null comment 'MIME',
  size_bytes bigint not null default 0 comment '字节大小',
  sha256 varchar(64) not null comment '内容哈希',
  storage_path varchar(512) not null comment '磁盘相对路径',
  biz_type varchar(64) null comment '业务类型',
  biz_id bigint null comment '业务ID',
  status varchar(32) not null default 'ACTIVE' comment '状态',
  created_by varchar(128) null comment '上传人',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_file_object_tenant (tenant_id, deleted, created_at),
  key idx_file_object_biz (tenant_id, biz_type, biz_id, deleted)
) engine=InnoDB default charset=utf8mb4 comment='文件对象';
