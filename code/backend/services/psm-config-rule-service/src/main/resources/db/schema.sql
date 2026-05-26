create table if not exists config_item (
    id bigint primary key auto_increment,
    tenant_id bigint not null,
    config_type varchar(64) not null,
    config_code varchar(128) not null,
    config_name varchar(255) not null,
    version_no int not null default 1,
    status varchar(32) not null default 'DRAFT',
    biz_scene varchar(64),
    content_json text,
    remark varchar(512),
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    deleted tinyint(1) not null default 0,
    unique key uk_config_item_code_version (tenant_id, config_type, config_code, version_no)
);

create table if not exists audit_change_log (
    id bigint primary key auto_increment,
    tenant_id bigint not null,
    operator_name varchar(128) not null,
    action varchar(64) not null,
    biz_type varchar(64) not null,
    biz_id bigint,
    before_value text,
    after_value text,
    result varchar(32) not null,
    operated_at datetime not null
);
