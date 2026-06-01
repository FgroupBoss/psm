-- 盲板抽堵 / 动土 / 断路专项表（暂不自动执行，手工落库）

CREATE TABLE IF NOT EXISTS blind_plate_registry (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  blind_plate_no VARCHAR(64) NOT NULL COMMENT '盲板编号',
  pipeline_id VARCHAR(128) NOT NULL COMMENT '管线标识',
  position VARCHAR(512) NOT NULL COMMENT '安装位置描述',
  diagram_ref VARCHAR(255) NULL COMMENT '位置图附件',
  spec VARCHAR(128) NULL COMMENT '规格',
  material VARCHAR(128) NULL COMMENT '材质',
  tag_no VARCHAR(128) NULL COMMENT '挂牌编号',
  status VARCHAR(32) NOT NULL DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE/IN_USE/REMOVED',
  version_no INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_bpr_no (tenant_id, blind_plate_no, deleted),
  KEY idx_bpr_pipeline (tenant_id, pipeline_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盲板台账';

CREATE TABLE IF NOT EXISTS blind_plate_work_detail (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL COMMENT '作业票ID',
  blind_plate_id BIGINT NOT NULL COMMENT '盲板台账ID',
  operation_type VARCHAR(16) NOT NULL COMMENT 'INSTALL/REMOVE',
  pipeline_id VARCHAR(128) NOT NULL,
  position_description VARCHAR(512) NOT NULL,
  position_diagram_ref VARCHAR(255) NOT NULL,
  medium_name VARCHAR(128) NOT NULL,
  temperature VARCHAR(64) NULL,
  pressure VARCHAR(64) NULL,
  hazard_json TEXT NULL COMMENT '危险特性JSON',
  tag_no VARCHAR(128) NOT NULL,
  spec VARCHAR(128) NULL,
  material VARCHAR(128) NULL,
  action_confirmed TINYINT NOT NULL DEFAULT 0 COMMENT '动作是否已确认',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_bpwd_permit (tenant_id, work_permit_id, deleted),
  KEY idx_bpwd_plate (tenant_id, blind_plate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盲板抽堵作业详情';

CREATE TABLE IF NOT EXISTS blind_plate_action_record (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  blind_plate_id BIGINT NOT NULL,
  from_status VARCHAR(32) NOT NULL,
  to_status VARCHAR(32) NOT NULL,
  attachment_ref VARCHAR(255) NULL,
  operated_by VARCHAR(128) NULL,
  operated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_bpar_permit (tenant_id, work_permit_id, operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盲板动作记录';

CREATE TABLE IF NOT EXISTS excavation_work_detail (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  area_geo_json TEXT NULL COMMENT '开挖范围GeoJSON',
  depth_m DECIMAL(8,2) NOT NULL COMMENT '开挖深度(米)',
  area_m2 DECIMAL(12,2) NULL COMMENT '开挖面积(平方米)',
  method VARCHAR(32) NOT NULL COMMENT 'MANUAL/MACHINE/MIXED',
  drawing_ref VARCHAR(255) NOT NULL COMMENT '施工图纸附件',
  valid_until DATETIME NULL COMMENT '票证有效截止时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_ewd_permit (tenant_id, work_permit_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动土作业详情';

CREATE TABLE IF NOT EXISTS excavation_underground_facility (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  facility_type VARCHAR(64) NOT NULL COMMENT '设施类型',
  owner_unit VARCHAR(128) NULL COMMENT '权属单位',
  position VARCHAR(512) NOT NULL,
  depth_m DECIMAL(8,2) NULL,
  detection_method VARCHAR(64) NULL COMMENT '探测方法',
  confirmed TINYINT NOT NULL DEFAULT 0 COMMENT '是否已确认',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_euf_permit (tenant_id, work_permit_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动土地下设施';

CREATE TABLE IF NOT EXISTS excavation_countersign (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  specialty VARCHAR(64) NOT NULL COMMENT '会签专业',
  signer_id BIGINT NOT NULL COMMENT '会签人ID',
  result VARCHAR(16) NOT NULL COMMENT 'APPROVED/REJECTED/PENDING',
  opinion VARCHAR(512) NULL,
  signed_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_ecs_permit (tenant_id, work_permit_id, specialty, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动土专业会签';

CREATE TABLE IF NOT EXISTS excavation_site_check (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  stage VARCHAR(16) NOT NULL COMMENT 'SITE_PERMIT/RESUME/MONITOR',
  item_code VARCHAR(64) NOT NULL,
  item_name VARCHAR(255) NOT NULL,
  check_result VARCHAR(16) NOT NULL COMMENT 'PASS/FAIL/NA',
  attachment_ref VARCHAR(255) NULL,
  checked_by VARCHAR(128) NULL,
  checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_esc_permit (tenant_id, work_permit_id, stage, checked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动土现场检查';

CREATE TABLE IF NOT EXISTS road_break_detail (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  road_id VARCHAR(128) NOT NULL COMMENT '道路标识',
  area_geo_json TEXT NULL COMMENT '断路范围GeoJSON',
  reason VARCHAR(512) NOT NULL COMMENT '断路原因',
  start_at DATETIME NOT NULL,
  end_at DATETIME NOT NULL,
  responsible_unit VARCHAR(128) NOT NULL COMMENT '责任单位',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_rbd_permit (tenant_id, work_permit_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='断路作业详情';

CREATE TABLE IF NOT EXISTS road_break_traffic_plan (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  detour_geo_json TEXT NULL COMMENT '绕行路线GeoJSON',
  emergency_lane_geo_json TEXT NULL COMMENT '应急通道GeoJSON',
  plan_ref VARCHAR(255) NOT NULL COMMENT '交通组织方案附件',
  confirmed TINYINT NOT NULL DEFAULT 0 COMMENT '方案是否确认',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_rbtp_permit (tenant_id, work_permit_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='断路交通方案';

CREATE TABLE IF NOT EXISTS road_break_site_control (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tenant_id BIGINT NOT NULL,
  work_permit_id BIGINT NOT NULL,
  item_code VARCHAR(64) NOT NULL,
  item_name VARCHAR(255) NOT NULL,
  position VARCHAR(512) NULL,
  check_result VARCHAR(16) NOT NULL COMMENT 'PASS/FAIL/NA',
  attachment_ref VARCHAR(255) NULL,
  checked_by VARCHAR(128) NULL,
  checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_rbsc_permit (tenant_id, work_permit_id, checked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='断路现场布控';
