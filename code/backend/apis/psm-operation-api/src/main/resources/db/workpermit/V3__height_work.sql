-- 高处作业专项表（暂不自动执行，手工落库）
CREATE TABLE IF NOT EXISTS height_work_detail (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  work_permit_id BIGINT NOT NULL COMMENT '作业票ID',
  work_height_m DECIMAL(8,2) NOT NULL COMMENT '距坠落基准面高度(米)',
  fall_datum_description VARCHAR(512) NOT NULL COMMENT '坠落高度基准面说明',
  work_location VARCHAR(512) NOT NULL COMMENT '具体作业位置',
  work_method VARCHAR(32) NOT NULL COMMENT 'SCAFFOLD/PLATFORM/GONDOLA/LADDER/OTHER',
  height_level VARCHAR(16) NOT NULL COMMENT 'LEVEL_1/LEVEL_2/LEVEL_3/LEVEL_4',
  risk_class VARCHAR(8) NOT NULL COMMENT 'A/B',
  manual_upgrade_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否人工提高控制等级',
  manual_upgrade_reason VARCHAR(512) NULL COMMENT '人工提高等级原因',
  rescue_plan_ref VARCHAR(255) NULL COMMENT '应急救援方案附件',
  rescue_contact VARCHAR(128) NULL COMMENT '救援联络人',
  communication_confirmed TINYINT NOT NULL DEFAULT 0 COMMENT '通信联络是否确认',
  rule_version VARCHAR(64) NOT NULL COMMENT '分级分类规则版本',
  valid_until DATETIME NOT NULL COMMENT '票证有效截止时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_hwd_permit (tenant_id, work_permit_id, deleted),
  KEY idx_hwd_level (tenant_id, height_level, risk_class, valid_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高处作业专项详情';

CREATE TABLE IF NOT EXISTS height_work_hazard_factor (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  factor_code VARCHAR(64) NOT NULL COMMENT '客观危险因素编码',
  factor_name VARCHAR(255) NOT NULL,
  hit_source VARCHAR(32) NOT NULL COMMENT 'MANUAL/WEATHER/RULE/INTEGRATION',
  control_measure VARCHAR(1024) NULL,
  attachment_ref VARCHAR(255) NULL,
  confirmed_by VARCHAR(128) NULL,
  confirmed_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_hwhf_permit (tenant_id, work_permit_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高处作业客观危险因素';

CREATE TABLE IF NOT EXISTS height_work_protection_check (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  check_stage VARCHAR(16) NOT NULL COMMENT 'SITE_PERMIT/RESUME/MONITOR',
  item_code VARCHAR(64) NOT NULL,
  item_name VARCHAR(255) NOT NULL,
  required_flag TINYINT NOT NULL DEFAULT 1,
  check_result VARCHAR(16) NOT NULL COMMENT 'PASS/FAIL/NA',
  attachment_ref VARCHAR(255) NULL,
  location_text VARCHAR(255) NULL,
  checked_by VARCHAR(128) NULL,
  checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_hwpc_permit (tenant_id, work_permit_id, check_stage, checked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高处作业防护检查';

CREATE TABLE IF NOT EXISTS height_work_environment_check (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  check_stage VARCHAR(16) NOT NULL COMMENT 'SITE_PERMIT/RESUME/MONITOR',
  wind_level VARCHAR(32) NULL,
  weather_type VARCHAR(32) NULL,
  temperature_c DECIMAL(6,2) NULL,
  visibility_m DECIMAL(10,2) NULL,
  illumination_lux DECIMAL(10,2) NULL,
  ground_condition VARCHAR(255) NULL,
  power_proximity_flag TINYINT NOT NULL DEFAULT 0,
  data_source VARCHAR(32) NOT NULL COMMENT 'MANUAL/WEATHER_SERVICE/SENSOR',
  source_sampled_at DATETIME NULL COMMENT '上游数据采集时间',
  check_result VARCHAR(16) NOT NULL COMMENT 'PASS/FAIL/MANUAL_REVIEW',
  review_reason VARCHAR(512) NULL,
  checked_by VARCHAR(128) NULL,
  checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_hwec_permit (tenant_id, work_permit_id, check_stage, checked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高处作业环境检查';
