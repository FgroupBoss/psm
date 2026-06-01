create table if not exists contractor_company (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  company_code varchar(128) not null comment '单位编码',
  company_name varchar(255) not null comment '单位名称',
  contact_name varchar(128) null comment '联系人',
  contact_phone varchar(64) null comment '联系电话',
  business_scope varchar(512) null comment '业务范围',
  status varchar(32) not null default 'DRAFT' comment '准入状态',
  blacklist_flag tinyint not null default 0 comment '是否黑名单',
  remark varchar(512) null comment '备注',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_contractor_company_code (tenant_id, company_code, deleted),
  key idx_contractor_company_status (tenant_id, status)
) engine=InnoDB default charset=utf8mb4 comment='承包商单位';

create table if not exists contractor_qualification (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  company_id bigint not null comment '单位ID',
  qual_type varchar(64) not null comment '资质类型',
  qual_name varchar(255) not null comment '资质名称',
  qual_no varchar(128) null comment '证书编号',
  valid_from date null comment '有效期起',
  valid_to date null comment '有效期止',
  core_flag tinyint not null default 0 comment '是否核心资质',
  file_id bigint null comment '附件文件ID',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_contractor_qual_company (tenant_id, company_id),
  key idx_contractor_qual_valid (tenant_id, valid_to)
) engine=InnoDB default charset=utf8mb4 comment='承包商单位资质';

create table if not exists contractor_worker (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  company_id bigint not null comment '所属单位ID',
  worker_code varchar(128) not null comment '人员编码',
  name varchar(128) not null comment '姓名',
  id_no_hash varchar(128) null comment '证件号哈希',
  phone_masked varchar(32) null comment '脱敏手机号',
  trade_type varchar(64) null comment '工种',
  access_status varchar(32) not null default 'INCOMPLETE' comment '准入状态',
  training_status varchar(32) null comment '培训状态',
  certificate_status varchar(32) null comment '证书状态',
  gate_card_no varchar(64) null comment '门禁卡号',
  location_tag_no varchar(64) null comment '定位标签',
  status varchar(32) not null default 'ENABLED' comment '记录状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  unique key uk_contractor_worker_code (tenant_id, worker_code, deleted),
  key idx_contractor_worker_company (tenant_id, company_id),
  key idx_contractor_worker_access (tenant_id, access_status)
) engine=InnoDB default charset=utf8mb4 comment='承包商人员';

create table if not exists worker_certificate (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  worker_id bigint not null comment '人员ID',
  cert_type varchar(64) not null comment '证书类型',
  cert_no varchar(128) null comment '证书编号',
  valid_from date null comment '有效期起',
  valid_to date null comment '有效期止',
  file_id bigint null comment '附件文件ID',
  status varchar(32) not null default 'ENABLED' comment '状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_worker_certificate_worker (tenant_id, worker_id),
  key idx_worker_certificate_valid (tenant_id, valid_to)
) engine=InnoDB default charset=utf8mb4 comment='承包商人员证书';

create table if not exists worker_training_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  worker_id bigint not null comment '人员ID',
  training_name varchar(255) not null comment '培训名称',
  training_result varchar(32) not null comment '培训结果',
  valid_from date null comment '有效期起',
  valid_to date null comment '有效期止',
  file_id bigint null comment '附件文件ID',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_worker_training_worker (tenant_id, worker_id)
) engine=InnoDB default charset=utf8mb4 comment='承包商人员培训记录';

create table if not exists contractor_violation (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  company_id bigint null comment '单位ID',
  worker_id bigint null comment '人员ID',
  violation_time datetime not null comment '违章时间',
  violation_desc varchar(1024) not null comment '违章描述',
  severity varchar(32) null comment '严重程度',
  rectification_status varchar(32) null comment '整改状态',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_contractor_violation_worker (tenant_id, worker_id),
  key idx_contractor_violation_company (tenant_id, company_id)
) engine=InnoDB default charset=utf8mb4 comment='承包商违章记录';

create table if not exists contractor_blacklist (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  target_type varchar(32) not null comment '对象类型 COMPANY/WORKER',
  target_id bigint not null comment '对象ID',
  reason varchar(512) not null comment '拉黑原因',
  effective_at datetime not null comment '生效时间',
  released_at datetime null comment '解除时间',
  status varchar(32) not null default 'ACTIVE' comment '状态',
  operator_id bigint null comment '操作人ID',
  operator_name varchar(128) null comment '操作人姓名',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  deleted tinyint not null default 0 comment '删除标识',
  primary key (id),
  key idx_contractor_blacklist_target (tenant_id, target_type, target_id)
) engine=InnoDB default charset=utf8mb4 comment='承包商黑名单';

create table if not exists contractor_audit_record (
  id bigint not null auto_increment comment '主键',
  tenant_id bigint not null comment '租户ID',
  target_type varchar(32) not null comment '对象类型',
  target_id bigint not null comment '对象ID',
  action varchar(64) not null comment '动作',
  before_status varchar(32) null comment '变更前状态',
  after_status varchar(32) null comment '变更后状态',
  opinion varchar(512) null comment '审核意见',
  operator_id bigint null comment '操作人ID',
  operator_name varchar(128) null comment '操作人姓名',
  operated_at datetime not null default current_timestamp comment '操作时间',
  primary key (id),
  key idx_contractor_audit_target (tenant_id, target_type, target_id),
  key idx_contractor_audit_time (tenant_id, operated_at)
) engine=InnoDB default charset=utf8mb4 comment='承包商准入审核流水';
