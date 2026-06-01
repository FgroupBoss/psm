-- ============================================================
-- PSM 平台数据库初始化脚本
-- 领域: realtime / risk / process_safety / incident_governance
-- 引擎: InnoDB, 字符集: utf8mb4
-- ============================================================

-- ============================================================
-- DATABASE: psm_realtime (实时感知域: alarm + location + video)
-- ============================================================
USE psm_realtime;

-- ---------- ALARM 告警模块 (7 tables) ----------
CREATE TABLE IF NOT EXISTS alarm_event (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  alarm_no VARCHAR(64) NOT NULL COMMENT '告警编号',
  source_type VARCHAR(64) NULL COMMENT '来源类型',
  source_code VARCHAR(128) NULL COMMENT '来源编码',
  title VARCHAR(255) NULL COMMENT '标题',
  content VARCHAR(1024) NULL COMMENT '内容',
  alarm_level VARCHAR(32) NULL COMMENT '告警级别',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  area_id BIGINT NULL COMMENT '区域ID',
  unit_id BIGINT NULL COMMENT '装置ID',
  equipment_id BIGINT NULL COMMENT '设备ID',
  monitor_point_id BIGINT NULL COMMENT '监测点位ID',
  hazard_id BIGINT NULL COMMENT '危险源ID',
  dedup_key VARCHAR(128) NULL COMMENT '去重键',
  occurrence_count INT NOT NULL DEFAULT 1 COMMENT '发生次数',
  first_occurred_at DATETIME NULL COMMENT '首次发生时间',
  last_occurred_at DATETIME NULL COMMENT '最近发生时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_alarm_event_no (tenant_id, alarm_no, deleted),
  KEY idx_alarm_event_status (tenant_id, status, last_occurred_at),
  KEY idx_alarm_event_area (tenant_id, area_id, status),
  KEY idx_alarm_event_hazard (tenant_id, hazard_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警事件';

CREATE TABLE IF NOT EXISTS alarm_occurrence (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  alarm_event_id BIGINT NOT NULL COMMENT '告警事件ID',
  occurred_at DATETIME NULL COMMENT '发生时间',
  raw_value VARCHAR(255) NULL COMMENT '原始值',
  snapshot_json MEDIUMTEXT NULL COMMENT '快照JSON',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_alarm_occurrence_event (tenant_id, alarm_event_id, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警发生记录';

CREATE TABLE IF NOT EXISTS alarm_action_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  alarm_event_id BIGINT NOT NULL COMMENT '告警事件ID',
  action_type VARCHAR(32) NOT NULL COMMENT '动作类型',
  action_content VARCHAR(1024) NULL COMMENT '动作内容',
  operator_id BIGINT NULL COMMENT '操作人ID',
  operator_name VARCHAR(128) NULL COMMENT '操作人姓名',
  operated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (id),
  KEY idx_alarm_action_event (tenant_id, alarm_event_id, operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警处置记录';

CREATE TABLE IF NOT EXISTS alarm_rule (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  rule_code VARCHAR(64) NOT NULL COMMENT '规则编码',
  rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
  rule_type VARCHAR(32) NOT NULL COMMENT '规则类型',
  config_json MEDIUMTEXT NULL COMMENT '配置JSON',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_alarm_rule_code (tenant_id, rule_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则';

CREATE TABLE IF NOT EXISTS alarm_dedup_rule (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  rule_code VARCHAR(64) NOT NULL COMMENT '规则编码',
  source_type VARCHAR(64) NULL COMMENT '来源类型',
  window_seconds INT NULL COMMENT '去重窗口秒数',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_alarm_dedup_code (tenant_id, rule_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警去重规则';

CREATE TABLE IF NOT EXISTS alarm_escalation_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  alarm_event_id BIGINT NOT NULL COMMENT '告警事件ID',
  escalation_level VARCHAR(32) NOT NULL COMMENT '升级级别',
  reason VARCHAR(512) NULL COMMENT '升级原因',
  operator_name VARCHAR(128) NULL COMMENT '操作人',
  escalated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '升级时间',
  PRIMARY KEY (id),
  KEY idx_alarm_esc_event (tenant_id, alarm_event_id, escalated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警升级记录';

CREATE TABLE IF NOT EXISTS alarm_notification_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  alarm_event_id BIGINT NOT NULL COMMENT '告警事件ID',
  channel VARCHAR(32) NOT NULL COMMENT '通知渠道',
  notify_target VARCHAR(255) NULL COMMENT '通知目标',
  notify_content VARCHAR(1024) NULL COMMENT '通知内容',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '发送状态',
  sent_at DATETIME NULL COMMENT '发送时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_alarm_notify_event (tenant_id, alarm_event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警通知记录';

-- ---------- LOCATION 定位模块 (10 tables) ----------
CREATE TABLE IF NOT EXISTS loc_realtime (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  tag_no VARCHAR(64) NOT NULL COMMENT '标签号',
  person_id BIGINT NULL COMMENT '人员ID',
  area_id BIGINT NULL COMMENT '区域ID',
  latitude DECIMAL(12,8) NULL COMMENT '纬度',
  longitude DECIMAL(12,8) NULL COMMENT '经度',
  online_status VARCHAR(32) NULL COMMENT '在线状态',
  last_seen_at DATETIME NULL COMMENT '最后在线时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_loc_realtime_tag (tenant_id, tag_no),
  KEY idx_loc_realtime_area (tenant_id, area_id, online_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实时定位';

CREATE TABLE IF NOT EXISTS loc_tag (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  tag_no VARCHAR(64) NOT NULL COMMENT '标签号',
  tag_type VARCHAR(32) NULL COMMENT '标签类型',
  vendor_code VARCHAR(64) NULL COMMENT '厂商编码',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  remark VARCHAR(512) NULL COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_loc_tag_no (tenant_id, tag_no, deleted),
  KEY idx_loc_tag_status (tenant_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定位标签';

CREATE TABLE IF NOT EXISTS loc_tag_binding (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  tag_no VARCHAR(64) NOT NULL COMMENT '标签号',
  person_type VARCHAR(32) NOT NULL COMMENT '人员类型',
  person_id BIGINT NOT NULL COMMENT '人员ID',
  contractor_id BIGINT NULL COMMENT '承包商ID',
  bind_at DATETIME NULL COMMENT '绑定时间',
  unbind_at DATETIME NULL COMMENT '解绑时间',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_loc_bind_tag (tenant_id, tag_no, status),
  KEY idx_loc_bind_person (tenant_id, person_type, person_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签绑定记录';

CREATE TABLE IF NOT EXISTS loc_track_point (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  tag_no VARCHAR(64) NOT NULL COMMENT '标签号',
  person_id BIGINT NULL COMMENT '人员ID',
  area_id BIGINT NULL COMMENT '区域ID',
  latitude DECIMAL(12,8) NULL COMMENT '纬度',
  longitude DECIMAL(12,8) NULL COMMENT '经度',
  recorded_at DATETIME NOT NULL COMMENT '记录时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_loc_track_tag (tenant_id, tag_no, recorded_at),
  KEY idx_loc_track_time (tenant_id, recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定位轨迹点';

CREATE TABLE IF NOT EXISTS loc_geofence (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  fence_code VARCHAR(64) NOT NULL COMMENT '围栏编码',
  fence_name VARCHAR(255) NOT NULL COMMENT '围栏名称',
  area_id BIGINT NULL COMMENT '区域ID',
  fence_type VARCHAR(32) NULL COMMENT '围栏类型',
  geometry_json MEDIUMTEXT NULL COMMENT 'GeoJSON几何',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_loc_fence_code (tenant_id, fence_code, deleted),
  KEY idx_loc_fence_area (tenant_id, area_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电子围栏';

CREATE TABLE IF NOT EXISTS loc_geofence_rule (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  fence_id BIGINT NOT NULL COMMENT '围栏ID',
  rule_type VARCHAR(32) NOT NULL COMMENT '规则类型',
  threshold_value INT NULL COMMENT '阈值',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_loc_fence_rule (tenant_id, fence_id, enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='围栏规则';

CREATE TABLE IF NOT EXISTS loc_event (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  event_type VARCHAR(32) NOT NULL COMMENT '事件类型',
  tag_no VARCHAR(64) NULL COMMENT '标签号',
  person_id BIGINT NULL COMMENT '人员ID',
  area_id BIGINT NULL COMMENT '区域ID',
  fence_id BIGINT NULL COMMENT '围栏ID',
  event_time DATETIME NOT NULL COMMENT '事件时间',
  alarm_id BIGINT NULL COMMENT '关联告警ID',
  work_permit_id BIGINT NULL COMMENT '关联作业票ID',
  raw_payload MEDIUMTEXT NULL COMMENT '原始数据',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_loc_event_time (tenant_id, event_time),
  KEY idx_loc_event_type (tenant_id, event_type, event_time),
  KEY idx_loc_event_alarm (tenant_id, alarm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定位事件';

CREATE TABLE IF NOT EXISTS gate_access_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  gate_code VARCHAR(64) NOT NULL COMMENT '门禁编码',
  card_no VARCHAR(64) NULL COMMENT '卡号',
  tag_no VARCHAR(64) NULL COMMENT '标签号',
  person_id BIGINT NULL COMMENT '人员ID',
  direction VARCHAR(8) NOT NULL COMMENT '进出方向',
  access_time DATETIME NOT NULL COMMENT '通行时间',
  access_result VARCHAR(32) NULL COMMENT '通行结果',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_gate_time (tenant_id, access_time),
  KEY idx_gate_person (tenant_id, person_id, access_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门禁通行记录';

CREATE TABLE IF NOT EXISTS vehicle_access_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  plate_no VARCHAR(32) NOT NULL COMMENT '车牌号',
  vehicle_type VARCHAR(32) NULL COMMENT '车辆类型',
  gate_code VARCHAR(64) NULL COMMENT '门禁编码',
  direction VARCHAR(8) NOT NULL COMMENT '进出方向',
  access_time DATETIME NOT NULL COMMENT '通行时间',
  driver_name VARCHAR(128) NULL COMMENT '驾驶员',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_vehicle_time (tenant_id, access_time),
  KEY idx_vehicle_plate (tenant_id, plate_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆通行记录';

CREATE TABLE IF NOT EXISTS visitor_access_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  visitor_name VARCHAR(128) NOT NULL COMMENT '访客姓名',
  id_card_no VARCHAR(64) NULL COMMENT '证件号',
  company_name VARCHAR(255) NULL COMMENT '来访单位',
  host_person_id BIGINT NULL COMMENT '受访人ID',
  gate_code VARCHAR(64) NULL COMMENT '门禁编码',
  tag_no VARCHAR(64) NULL COMMENT '访客标签',
  visit_purpose VARCHAR(255) NULL COMMENT '来访事由',
  direction VARCHAR(8) NOT NULL COMMENT '进出方向',
  access_time DATETIME NOT NULL COMMENT '通行时间',
  access_result VARCHAR(32) NULL COMMENT '通行结果',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_visitor_time (tenant_id, access_time),
  KEY idx_visitor_host (tenant_id, host_person_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客通行记录';

-- ---------- VIDEO 视频模块 (5 tables) ----------
CREATE TABLE IF NOT EXISTS video_camera (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  camera_code VARCHAR(64) NOT NULL COMMENT '摄像头编码',
  camera_name VARCHAR(255) NOT NULL COMMENT '摄像头名称',
  platform_code VARCHAR(64) NULL COMMENT '平台编码',
  platform_camera_id VARCHAR(128) NULL COMMENT '平台摄像头ID',
  area_id BIGINT NULL COMMENT '区域ID',
  major_hazard_id BIGINT NULL COMMENT '重大危险源ID',
  location_desc VARCHAR(512) NULL COMMENT '位置描述',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_video_camera_code (tenant_id, camera_code, deleted),
  KEY idx_video_camera_area (tenant_id, area_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频摄像头';

CREATE TABLE IF NOT EXISTS video_ai_event (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  event_no VARCHAR(64) NOT NULL COMMENT '事件编号',
  source_platform VARCHAR(64) NULL COMMENT '来源平台',
  camera_id BIGINT NULL COMMENT '摄像头ID',
  event_type VARCHAR(64) NOT NULL COMMENT '事件类型',
  event_time DATETIME NOT NULL COMMENT '事件时间',
  area_id BIGINT NULL COMMENT '区域ID',
  major_hazard_id BIGINT NULL COMMENT '重大危险源ID',
  alarm_id BIGINT NULL COMMENT '告警ID',
  work_permit_id BIGINT NULL COMMENT '作业票ID',
  severity VARCHAR(32) NULL COMMENT '严重程度',
  status VARCHAR(32) NOT NULL DEFAULT 'NEW' COMMENT '状态',
  title VARCHAR(255) NULL COMMENT '标题',
  description VARCHAR(1024) NULL COMMENT '描述',
  ignore_reason VARCHAR(512) NULL COMMENT '忽略原因',
  dedup_key VARCHAR(128) NULL COMMENT '去重键',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_video_ai_event_no (tenant_id, event_no, deleted),
  KEY idx_video_ai_event_time (tenant_id, event_time),
  KEY idx_video_ai_event_camera (tenant_id, camera_id, event_time),
  KEY idx_video_ai_event_status (tenant_id, status, event_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频AI事件';

CREATE TABLE IF NOT EXISTS video_ai_event_media (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  event_id BIGINT NOT NULL COMMENT '事件ID',
  media_type VARCHAR(16) NOT NULL COMMENT '媒体类型 image/video',
  file_id BIGINT NULL COMMENT '文件ID',
  media_url VARCHAR(512) NULL COMMENT '媒体URL',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_video_media_event (tenant_id, event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI事件媒体';

CREATE TABLE IF NOT EXISTS video_watch_session (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  work_permit_id BIGINT NULL COMMENT '作业票ID',
  camera_ids VARCHAR(512) NULL COMMENT '摄像头ID列表',
  operator_id BIGINT NULL COMMENT '操作人ID',
  operator_name VARCHAR(128) NULL COMMENT '操作人姓名',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  started_at DATETIME NULL COMMENT '开始时间',
  ended_at DATETIME NULL COMMENT '结束时间',
  remark VARCHAR(512) NULL COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_video_session_permit (tenant_id, work_permit_id),
  KEY idx_video_session_status (tenant_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频监护会话';

CREATE TABLE IF NOT EXISTS video_watch_capture (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  session_id BIGINT NOT NULL COMMENT '会话ID',
  camera_id BIGINT NULL COMMENT '摄像头ID',
  capture_type VARCHAR(32) NULL COMMENT '抓拍类型',
  file_id BIGINT NULL COMMENT '文件ID',
  media_url VARCHAR(512) NULL COMMENT '媒体URL',
  captured_at DATETIME NOT NULL COMMENT '抓拍时间',
  remark VARCHAR(512) NULL COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_video_capture_session (tenant_id, session_id),
  KEY idx_video_capture_time (tenant_id, captured_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频抓拍记录';


-- ============================================================
-- DATABASE: psm_risk (风险防控域: dualprevention + inspection + majorhazard)
-- ============================================================
USE psm_risk;

-- ---------- DUAL PREVENTION 双防模块 (6 tables) ----------
CREATE TABLE IF NOT EXISTS dp_risk_unit (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  parent_id BIGINT NULL COMMENT '上级风险单元ID',
  unit_code VARCHAR(64) NOT NULL COMMENT '单元编码',
  unit_name VARCHAR(128) NOT NULL COMMENT '单元名称',
  area_id BIGINT NULL COMMENT '区域ID',
  equipment_id BIGINT NULL COMMENT '设备ID',
  major_hazard_id BIGINT NULL COMMENT '重大危险源ID',
  inherent_risk_level VARCHAR(32) NULL COMMENT '固有风险等级',
  residual_risk_level VARCHAR(32) NULL COMMENT '残余风险等级',
  owner_org_id BIGINT NULL COMMENT '责任组织ID',
  owner_user_id BIGINT NULL COMMENT '责任人ID',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_dp_risk_unit_code (tenant_id, unit_code, deleted),
  KEY idx_dp_risk_unit_area (tenant_id, area_id),
  KEY idx_dp_risk_unit_parent (tenant_id, parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险单元';

CREATE TABLE IF NOT EXISTS dp_risk_event (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  risk_unit_id BIGINT NOT NULL COMMENT '风险单元ID',
  event_code VARCHAR(64) NOT NULL COMMENT '事件编码',
  event_name VARCHAR(255) NOT NULL COMMENT '事件名称',
  hazard_factors VARCHAR(1024) NULL COMMENT '危险因素',
  possible_consequence VARCHAR(1024) NULL COMMENT '可能后果',
  inherent_risk_level VARCHAR(32) NULL COMMENT '固有风险等级',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_dp_risk_event_code (tenant_id, event_code, deleted),
  KEY idx_dp_risk_event_unit (tenant_id, risk_unit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险事件';

CREATE TABLE IF NOT EXISTS dp_control_measure (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  risk_event_id BIGINT NOT NULL COMMENT '风险事件ID',
  measure_type VARCHAR(32) NOT NULL COMMENT '措施类型',
  measure_content VARCHAR(1024) NOT NULL COMMENT '措施内容',
  responsible_post VARCHAR(128) NULL COMMENT '责任岗位',
  check_cycle_days INT NULL COMMENT '检查周期天数',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_dp_measure_event (tenant_id, risk_event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管控措施';

CREATE TABLE IF NOT EXISTS dp_hazard_report (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  hazard_no VARCHAR(64) NOT NULL COMMENT '隐患编号',
  hazard_level VARCHAR(32) NOT NULL COMMENT '隐患等级',
  source_type VARCHAR(32) NOT NULL COMMENT '来源类型',
  source_biz_id BIGINT NULL COMMENT '来源业务ID',
  risk_unit_id BIGINT NULL COMMENT '风险单元ID',
  area_id BIGINT NULL COMMENT '区域ID',
  description VARCHAR(1024) NOT NULL COMMENT '隐患描述',
  found_at DATETIME NOT NULL COMMENT '发现时间',
  rectification_deadline DATETIME NULL COMMENT '整改期限',
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态',
  overdue_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否逾期',
  assignee_org_id BIGINT NULL COMMENT '整改组织ID',
  assignee_user_id BIGINT NULL COMMENT '整改人ID',
  contractor_id BIGINT NULL COMMENT '承包商ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_dp_hazard_no (tenant_id, hazard_no, deleted),
  KEY idx_dp_hazard_status (tenant_id, status, found_at),
  KEY idx_dp_hazard_area (tenant_id, area_id, status),
  KEY idx_dp_hazard_assignee (tenant_id, assignee_user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='隐患报告';

CREATE TABLE IF NOT EXISTS dp_hazard_action (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  hazard_id BIGINT NOT NULL COMMENT '隐患ID',
  action_type VARCHAR(32) NOT NULL COMMENT '动作类型',
  before_status VARCHAR(32) NULL COMMENT '变更前状态',
  after_status VARCHAR(32) NULL COMMENT '变更后状态',
  content VARCHAR(1024) NULL COMMENT '处置内容',
  evidence_file_ids VARCHAR(512) NULL COMMENT '附件ID列表',
  operator_id BIGINT NULL COMMENT '操作人ID',
  operator_name VARCHAR(128) NULL COMMENT '操作人姓名',
  operated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (id),
  KEY idx_dp_hazard_action (tenant_id, hazard_id, operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='隐患处置记录';

CREATE TABLE IF NOT EXISTS dp_hazard_escalation (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  hazard_id BIGINT NOT NULL COMMENT '隐患ID',
  reason VARCHAR(512) NULL COMMENT '升级原因',
  notify_user_id BIGINT NULL COMMENT '通知用户ID',
  operator_name VARCHAR(128) NULL COMMENT '操作人',
  escalated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '升级时间',
  PRIMARY KEY (id),
  KEY idx_dp_esc_hazard (tenant_id, hazard_id, escalated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='隐患升级记录';

-- ---------- INSPECTION 巡检模块 (10 tables) ----------
CREATE TABLE IF NOT EXISTS insp_route (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  route_code VARCHAR(64) NOT NULL COMMENT '路线编码',
  route_name VARCHAR(255) NOT NULL COMMENT '路线名称',
  area_id BIGINT NULL COMMENT '区域ID',
  estimated_minutes INT NULL COMMENT '预计耗时分钟',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  remark VARCHAR(512) NULL COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_insp_route_code (tenant_id, route_code, deleted),
  KEY idx_insp_route_area (tenant_id, area_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检路线';

CREATE TABLE IF NOT EXISTS insp_route_point (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  route_id BIGINT NOT NULL COMMENT '路线ID',
  point_code VARCHAR(64) NOT NULL COMMENT '点位编码',
  point_name VARCHAR(255) NOT NULL COMMENT '点位名称',
  sign_type VARCHAR(16) NOT NULL DEFAULT 'NFC' COMMENT '签到方式',
  sign_code VARCHAR(128) NULL COMMENT '签到码',
  area_id BIGINT NULL COMMENT '区域ID',
  checklist_template_id BIGINT NULL COMMENT '检查表模板ID',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_insp_point_code (tenant_id, point_code, deleted),
  KEY idx_insp_point_route (tenant_id, route_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检点位';

CREATE TABLE IF NOT EXISTS insp_checklist_template (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  template_code VARCHAR(64) NOT NULL COMMENT '模板编码',
  template_name VARCHAR(255) NOT NULL COMMENT '模板名称',
  category VARCHAR(64) NULL COMMENT '分类',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  remark VARCHAR(512) NULL COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_insp_template_code (tenant_id, template_code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检查表模板';

CREATE TABLE IF NOT EXISTS insp_checklist_item (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  template_id BIGINT NOT NULL COMMENT '模板ID',
  item_code VARCHAR(64) NOT NULL COMMENT '项目编码',
  item_name VARCHAR(255) NOT NULL COMMENT '项目名称',
  item_type VARCHAR(32) NULL COMMENT '项目类型',
  standard_value VARCHAR(255) NULL COMMENT '标准值',
  lower_limit DECIMAL(18,6) NULL COMMENT '下限',
  upper_limit DECIMAL(18,6) NULL COMMENT '上限',
  unit VARCHAR(32) NULL COMMENT '单位',
  abnormal_rule VARCHAR(64) NULL COMMENT '异常判定规则',
  photo_required TINYINT NOT NULL DEFAULT 0 COMMENT '是否拍照',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_insp_item_code (tenant_id, item_code, deleted),
  KEY idx_insp_item_template (tenant_id, template_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检查项目';

CREATE TABLE IF NOT EXISTS insp_plan (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  plan_code VARCHAR(64) NOT NULL COMMENT '计划编码',
  plan_name VARCHAR(255) NOT NULL COMMENT '计划名称',
  route_id BIGINT NULL COMMENT '路线ID',
  cycle_type VARCHAR(32) NOT NULL COMMENT '周期类型',
  cron_expr VARCHAR(128) NULL COMMENT 'Cron表达式',
  team_id BIGINT NULL COMMENT '班组ID',
  team_name VARCHAR(128) NULL COMMENT '班组名称',
  default_executor_id BIGINT NULL COMMENT '默认执行人',
  major_hazard_id BIGINT NULL COMMENT '重大危险源ID',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_insp_plan_code (tenant_id, plan_code, deleted),
  KEY idx_insp_plan_route (tenant_id, route_id),
  KEY idx_insp_plan_hazard (tenant_id, major_hazard_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检计划';

CREATE TABLE IF NOT EXISTS insp_task (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  task_no VARCHAR(64) NOT NULL COMMENT '任务编号',
  plan_id BIGINT NULL COMMENT '计划ID',
  route_id BIGINT NULL COMMENT '路线ID',
  scheduled_start DATETIME NULL COMMENT '计划开始',
  scheduled_end DATETIME NULL COMMENT '计划结束',
  actual_start DATETIME NULL COMMENT '实际开始',
  actual_end DATETIME NULL COMMENT '实际结束',
  executor_id BIGINT NULL COMMENT '执行人ID',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  completion_rate DECIMAL(5,2) NULL COMMENT '完成率',
  abnormal_count INT NOT NULL DEFAULT 0 COMMENT '异常数',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_insp_task_no (tenant_id, task_no, deleted),
  KEY idx_insp_task_plan (tenant_id, plan_id, status),
  KEY idx_insp_task_executor (tenant_id, executor_id, status),
  KEY idx_insp_task_status (tenant_id, status, scheduled_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检任务';

CREATE TABLE IF NOT EXISTS insp_task_item (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  task_id BIGINT NOT NULL COMMENT '任务ID',
  checklist_item_id BIGINT NULL COMMENT '检查项目ID',
  route_point_id BIGINT NULL COMMENT '巡检点位ID',
  result_value VARCHAR(255) NULL COMMENT '检测值',
  result_status VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT '结果 pass/fail/na',
  photo_urls VARCHAR(1024) NULL COMMENT '拍照URL',
  remark VARCHAR(512) NULL COMMENT '备注',
  checked_at DATETIME NULL COMMENT '检查时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_insp_task_item (tenant_id, task_id, result_status),
  KEY idx_insp_task_item_point (tenant_id, route_point_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检任务项';

CREATE TABLE IF NOT EXISTS insp_sign_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  task_id BIGINT NOT NULL COMMENT '任务ID',
  route_point_id BIGINT NOT NULL COMMENT '巡检点位ID',
  sign_type VARCHAR(16) NULL COMMENT '签到方式',
  sign_code VARCHAR(128) NULL COMMENT '签到码',
  signed_at DATETIME NOT NULL COMMENT '签到时间',
  operator_id BIGINT NULL COMMENT '操作人ID',
  operator_name VARCHAR(128) NULL COMMENT '操作人姓名',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_insp_sign_task (tenant_id, task_id),
  KEY idx_insp_sign_point (tenant_id, route_point_id, signed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录';

CREATE TABLE IF NOT EXISTS insp_abnormal_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  task_id BIGINT NOT NULL COMMENT '任务ID',
  task_item_id BIGINT NULL COMMENT '任务项ID',
  route_point_id BIGINT NULL COMMENT '巡检点位ID',
  abnormal_desc VARCHAR(1024) NOT NULL COMMENT '异常描述',
  photo_urls VARCHAR(1024) NULL COMMENT '拍照URL',
  severity VARCHAR(32) NULL COMMENT '严重程度',
  handle_status VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '处置状态',
  hazard_draft_id BIGINT NULL COMMENT '隐患草稿ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_insp_abnormal_task (tenant_id, task_id),
  KEY idx_insp_abnormal_status (tenant_id, handle_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检异常记录';

CREATE TABLE IF NOT EXISTS insp_task_draft (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  client_draft_id VARCHAR(128) NOT NULL COMMENT '客户端草稿ID',
  task_id BIGINT NULL COMMENT '任务ID',
  payload_json MEDIUMTEXT NOT NULL COMMENT '草稿内容',
  sync_status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '同步状态',
  last_error VARCHAR(512) NULL COMMENT '最近错误',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_insp_draft (tenant_id, client_draft_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检任务草稿';

-- ---------- MAJOR HAZARD 重大危险源模块 (6 tables) ----------
CREATE TABLE IF NOT EXISTS major_hazard (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  hazard_no VARCHAR(64) NOT NULL COMMENT '危险源编号',
  name VARCHAR(255) NOT NULL COMMENT '名称',
  hazard_type VARCHAR(64) NOT NULL COMMENT '危险源类型',
  level VARCHAR(32) NOT NULL COMMENT '等级',
  area_id BIGINT NULL COMMENT '区域ID',
  unit_id BIGINT NULL COMMENT '装置ID',
  material VARCHAR(255) NULL COMMENT '涉及物料',
  design_capacity VARCHAR(128) NULL COMMENT '设计能力',
  actual_capacity VARCHAR(128) NULL COMMENT '实际能力',
  critical_quantity VARCHAR(128) NULL COMMENT '临界量',
  emergency_plan_id BIGINT NULL COMMENT '应急预案ID',
  default_inspection_plan_id BIGINT NULL COMMENT '默认巡检计划ID',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  published_at DATETIME NULL COMMENT '发布日期',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_major_hazard_no (tenant_id, hazard_no, deleted),
  KEY idx_major_hazard_area (tenant_id, area_id, status),
  KEY idx_major_hazard_level (tenant_id, level, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='重大危险源';

CREATE TABLE IF NOT EXISTS major_hazard_point_rel (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  hazard_id BIGINT NOT NULL COMMENT '危险源ID',
  monitor_point_id BIGINT NOT NULL COMMENT '监测点位ID',
  point_code VARCHAR(128) NULL COMMENT '点位编码',
  point_name VARCHAR(255) NULL COMMENT '点位名称',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_mh_point_rel (tenant_id, hazard_id, monitor_point_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='危险源-监测点位关联';

CREATE TABLE IF NOT EXISTS major_hazard_attachment (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  hazard_id BIGINT NOT NULL COMMENT '危险源ID',
  attachment_type VARCHAR(32) NOT NULL COMMENT '附件类型',
  file_id BIGINT NOT NULL COMMENT '文件ID',
  file_name VARCHAR(255) NULL COMMENT '文件名',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_mh_attach_hazard (tenant_id, hazard_id, attachment_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='重大危险源附件';

CREATE TABLE IF NOT EXISTS major_hazard_responsibility (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  hazard_id BIGINT NOT NULL COMMENT '危险源ID',
  responsibility_type VARCHAR(32) NOT NULL COMMENT '责任类型',
  person_name VARCHAR(128) NOT NULL COMMENT '责任人',
  person_phone VARCHAR(32) NULL COMMENT '联系电话',
  person_id BIGINT NULL COMMENT '责任人ID',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_mh_resp_hazard (tenant_id, hazard_id, responsibility_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='重大危险源责任人包保';

CREATE TABLE IF NOT EXISTS major_hazard_status_log (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  hazard_id BIGINT NOT NULL COMMENT '危险源ID',
  from_status VARCHAR(32) NULL COMMENT '原状态',
  to_status VARCHAR(32) NOT NULL COMMENT '目标状态',
  reason VARCHAR(512) NULL COMMENT '变更原因',
  operator_id BIGINT NULL COMMENT '操作人ID',
  operator_name VARCHAR(128) NULL COMMENT '操作人',
  operated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (id),
  KEY idx_mh_status_hazard (tenant_id, hazard_id, operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='重大危险源状态日志';

CREATE TABLE IF NOT EXISTS major_hazard_audit_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  target_type VARCHAR(32) NOT NULL COMMENT '对象类型',
  target_id BIGINT NOT NULL COMMENT '对象ID',
  action VARCHAR(64) NOT NULL COMMENT '动作',
  before_status VARCHAR(32) NULL COMMENT '变更前状态',
  after_status VARCHAR(32) NULL COMMENT '变更后状态',
  opinion VARCHAR(512) NULL COMMENT '意见',
  operator_id BIGINT NULL COMMENT '操作人ID',
  operator_name VARCHAR(128) NULL COMMENT '操作人',
  operated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (id),
  KEY idx_mh_audit_target (tenant_id, target_type, target_id),
  KEY idx_mh_audit_time (tenant_id, operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='重大危险源操作审计';


-- ============================================================
-- DATABASE: psm_process_safety (过程安全域: barrier + configrule + moc + pha + pssr)
-- ============================================================
USE psm_process_safety;

-- ---------- BARRIER 屏障模块 (10 tables) ----------
CREATE TABLE IF NOT EXISTS barrier (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  barrier_code VARCHAR(64) NOT NULL COMMENT '屏障编码',
  barrier_name VARCHAR(255) NOT NULL COMMENT '屏障名称',
  barrier_type VARCHAR(32) NOT NULL COMMENT '屏障类型',
  major_hazard_id BIGINT NULL COMMENT '重大危险源ID',
  hazop_scenario_id BIGINT NULL COMMENT 'HAZOP场景ID',
  owner_org_id BIGINT NULL COMMENT '责任组织ID',
  health_score DECIMAL(5,2) NULL COMMENT '健康评分',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_barrier_code (tenant_id, barrier_code, deleted),
  KEY idx_barrier_type (tenant_id, barrier_type, status),
  KEY idx_barrier_hazard (tenant_id, major_hazard_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全屏障';

CREATE TABLE IF NOT EXISTS barrier_element (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  barrier_id BIGINT NOT NULL COMMENT '屏障ID',
  element_name VARCHAR(255) NOT NULL COMMENT '要素名称',
  element_type VARCHAR(32) NULL COMMENT '要素类型',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_barrier_element (tenant_id, barrier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='屏障要素';

CREATE TABLE IF NOT EXISTS barrier_degradation (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  barrier_id BIGINT NOT NULL COMMENT '屏障ID',
  from_status VARCHAR(32) NULL COMMENT '原状态',
  to_status VARCHAR(32) NOT NULL COMMENT '目标状态',
  reason VARCHAR(512) NULL COMMENT '降级原因',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_barrier_deg (tenant_id, barrier_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='屏障降级记录';

CREATE TABLE IF NOT EXISTS barrier_health_snapshot (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  barrier_id BIGINT NOT NULL COMMENT '屏障ID',
  health_score DECIMAL(5,2) NULL COMMENT '健康评分',
  snapshot_at DATETIME NOT NULL COMMENT '快照时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_barrier_snap (tenant_id, barrier_id, snapshot_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='屏障健康快照';

CREATE TABLE IF NOT EXISTS compensating_measure (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  barrier_id BIGINT NOT NULL COMMENT '屏障ID',
  measure_desc VARCHAR(1024) NOT NULL COMMENT '补偿措施描述',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_comp_measure (tenant_id, barrier_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补偿措施';

-- ---------- MI 机械完整性 (5 tables) ----------
CREATE TABLE IF NOT EXISTS mi_equipment (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  equipment_code VARCHAR(128) NOT NULL COMMENT '设备编码',
  equipment_name VARCHAR(255) NOT NULL COMMENT '设备名称',
  area_id BIGINT NULL COMMENT '区域ID',
  critical_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否关键设备',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_mi_equip_code (tenant_id, equipment_code, deleted),
  KEY idx_mi_equip_area (tenant_id, area_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机械完整性设备';

CREATE TABLE IF NOT EXISTS mi_defect (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  defect_no VARCHAR(64) NOT NULL COMMENT '缺陷编号',
  equipment_id BIGINT NOT NULL COMMENT '设备ID',
  defect_level VARCHAR(32) NOT NULL COMMENT '缺陷等级',
  source_type VARCHAR(32) NOT NULL COMMENT '来源类型',
  description VARCHAR(1024) NOT NULL COMMENT '缺陷描述',
  repair_deadline DATETIME NULL COMMENT '修复期限',
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_mi_defect_no (tenant_id, defect_no, deleted),
  KEY idx_mi_defect_equip (tenant_id, equipment_id, status),
  KEY idx_mi_defect_status (tenant_id, status, repair_deadline)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机械完整性缺陷';

CREATE TABLE IF NOT EXISTS mi_inspection_plan (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  equipment_id BIGINT NOT NULL COMMENT '设备ID',
  plan_name VARCHAR(255) NOT NULL COMMENT '计划名称',
  cycle_days INT NULL COMMENT '周期天数',
  next_due_at DATETIME NULL COMMENT '下次到期',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_mi_plan_equip (tenant_id, equipment_id, status),
  KEY idx_mi_plan_due (tenant_id, next_due_at, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MI检查计划';

CREATE TABLE IF NOT EXISTS mi_inspection_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  plan_id BIGINT NULL COMMENT '计划ID',
  equipment_id BIGINT NOT NULL COMMENT '设备ID',
  inspected_at DATETIME NOT NULL COMMENT '检查时间',
  result VARCHAR(32) NOT NULL COMMENT '检查结果',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_mi_record_equip (tenant_id, equipment_id, inspected_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MI检查记录';

CREATE TABLE IF NOT EXISTS mi_maintenance_task (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  defect_id BIGINT NULL COMMENT '缺陷ID',
  task_desc VARCHAR(1024) NOT NULL COMMENT '任务描述',
  owner_user_id BIGINT NULL COMMENT '负责人ID',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_mi_task_defect (tenant_id, defect_id),
  KEY idx_mi_task_owner (tenant_id, owner_user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MI维修任务';

-- ---------- CONFIG RULE 配置规则模块 (2 tables) ----------
CREATE TABLE IF NOT EXISTS config_item (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  config_type VARCHAR(64) NOT NULL COMMENT '配置类型',
  config_code VARCHAR(128) NOT NULL COMMENT '配置编码',
  config_name VARCHAR(255) NOT NULL COMMENT '配置名称',
  version_no INT NOT NULL DEFAULT 1 COMMENT '版本号',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  biz_scene VARCHAR(64) NULL COMMENT '业务场景',
  content_json MEDIUMTEXT NULL COMMENT '配置内容JSON',
  remark VARCHAR(512) NULL COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_config_item (tenant_id, config_type, config_code, deleted),
  KEY idx_config_item_status (tenant_id, config_type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置规则项';

CREATE TABLE IF NOT EXISTS config_audit_change_log (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  operator_name VARCHAR(128) NOT NULL COMMENT '操作人',
  action VARCHAR(64) NOT NULL COMMENT '动作',
  biz_type VARCHAR(64) NOT NULL COMMENT '业务类型',
  biz_id BIGINT NULL COMMENT '业务ID',
  before_value MEDIUMTEXT NULL COMMENT '变更前值',
  after_value MEDIUMTEXT NULL COMMENT '变更后值',
  result VARCHAR(32) NOT NULL COMMENT '结果',
  operated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (id),
  KEY idx_config_audit_biz (tenant_id, biz_type, biz_id),
  KEY idx_config_audit_time (tenant_id, operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配置变更审计';

-- ---------- MOC 变更管理模块 (7 tables) ----------
CREATE TABLE IF NOT EXISTS moc_change (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  change_no VARCHAR(64) NOT NULL COMMENT '变更编号',
  change_title VARCHAR(255) NOT NULL COMMENT '变更标题',
  change_type VARCHAR(32) NOT NULL COMMENT '变更类型',
  change_level VARCHAR(32) NOT NULL COMMENT '变更等级',
  temporary_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否临时变更',
  emergency_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否紧急变更',
  affected_area_id BIGINT NULL COMMENT '影响区域ID',
  affected_equipment_id BIGINT NULL COMMENT '影响设备ID',
  risk_level VARCHAR(32) NULL COMMENT '风险等级',
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_moc_change_no (tenant_id, change_no, deleted),
  KEY idx_moc_change_status (tenant_id, status, created_at),
  KEY idx_moc_change_area (tenant_id, affected_area_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MOC变更主表';

CREATE TABLE IF NOT EXISTS moc_impact_analysis (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  change_id BIGINT NOT NULL COMMENT '变更ID',
  discipline VARCHAR(64) NOT NULL COMMENT '专业',
  impact_desc VARCHAR(1024) NOT NULL COMMENT '影响描述',
  risk_level VARCHAR(32) NULL COMMENT '风险等级',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_moc_impact_change (tenant_id, change_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MOC影响分析';

CREATE TABLE IF NOT EXISTS moc_approval_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  change_id BIGINT NOT NULL COMMENT '变更ID',
  approver_user_id BIGINT NULL COMMENT '审批人ID',
  decision VARCHAR(32) NOT NULL COMMENT '审批决定',
  comment_text VARCHAR(1024) NULL COMMENT '审批意见',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_moc_approval_change (tenant_id, change_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MOC审批记录';

CREATE TABLE IF NOT EXISTS moc_close_condition (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  change_id BIGINT NOT NULL COMMENT '变更ID',
  condition_type VARCHAR(32) NOT NULL COMMENT '条件类型',
  required_flag TINYINT NOT NULL DEFAULT 1 COMMENT '是否必需',
  owner_user_id BIGINT NULL COMMENT '负责人ID',
  due_at DATETIME NULL COMMENT '到期时间',
  completed_at DATETIME NULL COMMENT '完成时间',
  evidence_file_id BIGINT NULL COMMENT '证据文件ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_moc_close_change (tenant_id, change_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MOC关闭条件';

CREATE TABLE IF NOT EXISTS moc_document_update (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  change_id BIGINT NOT NULL COMMENT '变更ID',
  doc_type VARCHAR(64) NOT NULL COMMENT '文档类型',
  doc_name VARCHAR(255) NOT NULL COMMENT '文档名称',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_moc_doc_change (tenant_id, change_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MOC文档更新';

CREATE TABLE IF NOT EXISTS moc_implementation_task (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  change_id BIGINT NOT NULL COMMENT '变更ID',
  task_desc VARCHAR(1024) NOT NULL COMMENT '任务描述',
  owner_user_id BIGINT NULL COMMENT '负责人ID',
  planned_at DATETIME NULL COMMENT '计划时间',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_moc_task_change (tenant_id, change_id),
  KEY idx_moc_task_owner (tenant_id, owner_user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MOC实施任务';

CREATE TABLE IF NOT EXISTS moc_training_requirement (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  change_id BIGINT NOT NULL COMMENT '变更ID',
  training_desc VARCHAR(1024) NOT NULL COMMENT '培训描述',
  owner_user_id BIGINT NULL COMMENT '负责人ID',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_moc_training_change (tenant_id, change_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MOC培训要求';

-- ---------- PHA 过程危害分析模块 (10 tables) ----------
CREATE TABLE IF NOT EXISTS pha_project (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  project_no VARCHAR(64) NOT NULL COMMENT '项目编号',
  project_name VARCHAR(255) NOT NULL COMMENT '项目名称',
  site_id BIGINT NULL COMMENT '厂区ID',
  unit_id BIGINT NULL COMMENT '装置ID',
  major_hazard_id BIGINT NULL COMMENT '重大危险源ID',
  method VARCHAR(32) NOT NULL COMMENT '分析方法 HAZOP/LOPA/What-If',
  version VARCHAR(32) NULL COMMENT '版本',
  review_due_at DATETIME NULL COMMENT '复审到期',
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pha_project_no (tenant_id, project_no, deleted),
  KEY idx_pha_project_status (tenant_id, status),
  KEY idx_pha_project_unit (tenant_id, unit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PHA项目';

CREATE TABLE IF NOT EXISTS pha_node (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  project_id BIGINT NOT NULL COMMENT '项目ID',
  node_no VARCHAR(32) NOT NULL COMMENT '节点编号',
  node_name VARCHAR(255) NOT NULL COMMENT '节点名称',
  design_intent VARCHAR(1024) NULL COMMENT '设计意图',
  parameters VARCHAR(512) NULL COMMENT '参数列表',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_pha_node_project (tenant_id, project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PHA节点';

CREATE TABLE IF NOT EXISTS hazop_deviation (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  node_id BIGINT NOT NULL COMMENT '节点ID',
  parameter VARCHAR(128) NOT NULL COMMENT '参数',
  guideword VARCHAR(64) NOT NULL COMMENT '引导词',
  deviation_desc VARCHAR(1024) NOT NULL COMMENT '偏差描述',
  risk_level VARCHAR(32) NULL COMMENT '风险等级',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_hazop_dev_node (tenant_id, node_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HAZOP偏差';

CREATE TABLE IF NOT EXISTS hazop_cause (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  deviation_id BIGINT NOT NULL COMMENT '偏差ID',
  cause_desc VARCHAR(1024) NOT NULL COMMENT '原因描述',
  frequency VARCHAR(32) NULL COMMENT '发生频率',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_hazop_cause_dev (tenant_id, deviation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HAZOP原因';

CREATE TABLE IF NOT EXISTS hazop_consequence (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  deviation_id BIGINT NOT NULL COMMENT '偏差ID',
  consequence_desc VARCHAR(1024) NOT NULL COMMENT '后果描述',
  severity VARCHAR(32) NULL COMMENT '严重程度',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_hazop_cons_dev (tenant_id, deviation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HAZOP后果';

CREATE TABLE IF NOT EXISTS hazop_safeguard (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  deviation_id BIGINT NOT NULL COMMENT '偏差ID',
  safeguard_type VARCHAR(32) NOT NULL COMMENT '保护措施类型',
  safeguard_desc VARCHAR(1024) NOT NULL COMMENT '保护措施描述',
  effectiveness VARCHAR(32) NULL COMMENT '有效性',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_hazop_safe_dev (tenant_id, deviation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HAZOP保护措施';

CREATE TABLE IF NOT EXISTS lopa_scenario (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  scenario_no VARCHAR(64) NOT NULL COMMENT '场景编号',
  project_id BIGINT NULL COMMENT '项目ID',
  deviation_id BIGINT NULL COMMENT '偏差ID',
  initiating_event_frequency DECIMAL(12,8) NULL COMMENT '初始事件频率',
  consequence_severity VARCHAR(32) NULL COMMENT '后果严重度',
  target_frequency DECIMAL(12,8) NULL COMMENT '目标频率',
  mitigated_frequency DECIMAL(12,8) NULL COMMENT '减缓后频率',
  sil_recommendation VARCHAR(64) NULL COMMENT 'SIL建议',
  calculation_version VARCHAR(32) NULL COMMENT '计算版本',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_lopa_scenario_no (tenant_id, scenario_no, deleted),
  KEY idx_lopa_scenario_project (tenant_id, project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LOPA场景';

CREATE TABLE IF NOT EXISTS lopa_ipl (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  scenario_id BIGINT NOT NULL COMMENT '场景ID',
  ipl_name VARCHAR(255) NOT NULL COMMENT 'IPL名称',
  pfd DECIMAL(12,8) NULL COMMENT '要求时失效概率',
  ipl_type VARCHAR(32) NULL COMMENT 'IPL类型',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_lopa_ipl_scenario (tenant_id, scenario_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LOPA独立保护层';

CREATE TABLE IF NOT EXISTS pha_recommendation (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  project_id BIGINT NULL COMMENT '项目ID',
  deviation_id BIGINT NULL COMMENT '偏差ID',
  rec_no VARCHAR(64) NOT NULL COMMENT '建议项编号',
  description VARCHAR(1024) NOT NULL COMMENT '建议描述',
  owner_user_id BIGINT NULL COMMENT '负责人ID',
  due_at DATETIME NULL COMMENT '到期时间',
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pha_rec_no (tenant_id, rec_no, deleted),
  KEY idx_pha_rec_project (tenant_id, project_id),
  KEY idx_pha_rec_status (tenant_id, status, due_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PHA建议项';

CREATE TABLE IF NOT EXISTS pha_review_plan (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  project_id BIGINT NOT NULL COMMENT '项目ID',
  plan_at DATETIME NOT NULL COMMENT '计划复审时间',
  reviewer_user_id BIGINT NULL COMMENT '复审人ID',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_pha_review_project (tenant_id, project_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PHA复审计划';

-- ---------- PSSR 启动前安全审查模块 (6 tables) ----------
CREATE TABLE IF NOT EXISTS pssr_project (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  pssr_no VARCHAR(64) NOT NULL COMMENT '审查编号',
  project_name VARCHAR(255) NOT NULL COMMENT '项目名称',
  source_type VARCHAR(32) NULL COMMENT '来源类型',
  source_biz_id BIGINT NULL COMMENT '来源业务ID',
  area_id BIGINT NULL COMMENT '区域ID',
  equipment_id BIGINT NULL COMMENT '设备ID',
  planned_startup_at DATETIME NULL COMMENT '计划开车时间',
  approval_status VARCHAR(32) NULL COMMENT '审批状态',
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pssr_project_no (tenant_id, pssr_no, deleted),
  KEY idx_pssr_project_status (tenant_id, status),
  KEY idx_pssr_project_source (tenant_id, source_type, source_biz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PSSR项目';

CREATE TABLE IF NOT EXISTS pssr_template (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  template_code VARCHAR(64) NOT NULL COMMENT '模板编码',
  template_name VARCHAR(255) NOT NULL COMMENT '模板名称',
  unit_type VARCHAR(64) NULL COMMENT '装置类型',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pssr_template_code (tenant_id, template_code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PSSR检查表模板';

CREATE TABLE IF NOT EXISTS pssr_check_item (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  template_id BIGINT NOT NULL COMMENT '模板ID',
  item_no VARCHAR(32) NOT NULL COMMENT '检查项编号',
  item_desc VARCHAR(1024) NOT NULL COMMENT '检查项描述',
  discipline VARCHAR(64) NULL COMMENT '专业',
  issue_level VARCHAR(32) NULL COMMENT '问题级别',
  startup_block_flag TINYINT NOT NULL DEFAULT 0 COMMENT '开车阻断标志',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_pssr_item_template (tenant_id, template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PSSR检查项';

CREATE TABLE IF NOT EXISTS pssr_execution_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  project_id BIGINT NOT NULL COMMENT '项目ID',
  check_item_id BIGINT NOT NULL COMMENT '检查项ID',
  result VARCHAR(16) NOT NULL COMMENT '检查结果 pass/fail',
  remark VARCHAR(512) NULL COMMENT '备注',
  executor_user_id BIGINT NULL COMMENT '执行人ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_pssr_exec_project (tenant_id, project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PSSR执行记录';

CREATE TABLE IF NOT EXISTS pssr_issue (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  project_id BIGINT NOT NULL COMMENT '项目ID',
  check_item_id BIGINT NULL COMMENT '检查项ID',
  issue_level VARCHAR(32) NOT NULL COMMENT '问题级别',
  description VARCHAR(1024) NOT NULL COMMENT '问题描述',
  owner_user_id BIGINT NULL COMMENT '负责人ID',
  due_at DATETIME NULL COMMENT '到期时间',
  close_required_before_startup TINYINT NOT NULL DEFAULT 1 COMMENT '开车前必须关闭',
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_pssr_issue_project (tenant_id, project_id),
  KEY idx_pssr_issue_status (tenant_id, status, due_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PSSR问题项';

CREATE TABLE IF NOT EXISTS pssr_approval_record (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  project_id BIGINT NOT NULL COMMENT '项目ID',
  approver_user_id BIGINT NULL COMMENT '审批人ID',
  decision VARCHAR(32) NOT NULL COMMENT '审批决定',
  comment_text VARCHAR(1024) NULL COMMENT '审批意见',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_pssr_approval_project (tenant_id, project_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PSSR审批记录';


-- ============================================================
-- DATABASE: psm_incident_governance (事件治理域: governance + incident + integration + report)
-- ============================================================
USE psm_incident_governance;

-- ---------- GOVERNANCE 治理看板模块 (11 tables) ----------
CREATE TABLE IF NOT EXISTS gov_site_mapping (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  site_code VARCHAR(64) NOT NULL COMMENT '站点编码',
  site_name VARCHAR(255) NOT NULL COMMENT '站点名称',
  org_id BIGINT NULL COMMENT '组织ID',
  enabled_flag TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_gov_site_code (tenant_id, site_code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集团站点映射';

CREATE TABLE IF NOT EXISTS gov_metric_definition (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  metric_code VARCHAR(64) NOT NULL COMMENT '指标编码',
  metric_name VARCHAR(255) NOT NULL COMMENT '指标名称',
  metric_domain VARCHAR(64) NOT NULL COMMENT '指标域',
  statistic_period VARCHAR(32) NULL COMMENT '统计周期',
  formula_version VARCHAR(32) NULL COMMENT '公式版本',
  target_value DECIMAL(18,4) NULL COMMENT '目标值',
  owner_org_id BIGINT NULL COMMENT '责任组织ID',
  enabled_flag TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_gov_metric_code (tenant_id, metric_code, deleted),
  KEY idx_gov_metric_domain (tenant_id, metric_domain, enabled_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治理指标定义';

CREATE TABLE IF NOT EXISTS gov_metric_snapshot (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  metric_code VARCHAR(64) NOT NULL COMMENT '指标编码',
  site_id BIGINT NULL COMMENT '站点ID',
  period_start DATETIME NULL COMMENT '统计周期开始',
  period_end DATETIME NULL COMMENT '统计周期结束',
  metric_value DECIMAL(18,4) NULL COMMENT '指标值',
  target_value DECIMAL(18,4) NULL COMMENT '目标值',
  calculation_time DATETIME NULL COMMENT '计算时间',
  input_hash VARCHAR(128) NULL COMMENT '输入哈希',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_gov_snapshot_metric (tenant_id, metric_code, site_id, period_start),
  KEY idx_gov_snapshot_time (tenant_id, calculation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治理指标快照';

CREATE TABLE IF NOT EXISTS gov_audit_issue (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  issue_no VARCHAR(64) NOT NULL COMMENT '问题编号',
  site_id BIGINT NULL COMMENT '站点ID',
  description VARCHAR(1024) NOT NULL COMMENT '问题描述',
  owner_user_id BIGINT NULL COMMENT '负责人ID',
  due_at DATETIME NULL COMMENT '到期时间',
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_gov_issue_no (tenant_id, issue_no, deleted),
  KEY idx_gov_issue_site (tenant_id, site_id, status),
  KEY idx_gov_issue_status (tenant_id, status, due_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治理审计问题';

CREATE TABLE IF NOT EXISTS gov_template (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  template_code VARCHAR(64) NOT NULL COMMENT '模板编码',
  template_name VARCHAR(255) NOT NULL COMMENT '模板名称',
  template_type VARCHAR(64) NULL COMMENT '模板类型',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_gov_template_code (tenant_id, template_code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治理报告模板';

CREATE TABLE IF NOT EXISTS gov_template_version (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  template_id BIGINT NOT NULL COMMENT '模板ID',
  version_no VARCHAR(32) NOT NULL COMMENT '版本号',
  content MEDIUMTEXT NULL COMMENT '模板内容',
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_gov_tpl_ver (tenant_id, template_id, version_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治理报告模板版本';

-- ---------- DW 数据仓库事实表 (5 tables) ----------
CREATE TABLE IF NOT EXISTS dw_alarm_fact (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  site_id BIGINT NULL COMMENT '站点ID',
  alarm_no VARCHAR(64) NULL COMMENT '告警编号',
  alarm_level VARCHAR(32) NULL COMMENT '告警级别',
  status VARCHAR(32) NULL COMMENT '状态',
  occurred_at DATETIME NULL COMMENT '发生时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_dw_alarm_site (tenant_id, site_id, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警数据仓库事实';

CREATE TABLE IF NOT EXISTS dw_hazard_fact (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  site_id BIGINT NULL COMMENT '站点ID',
  hazard_no VARCHAR(64) NULL COMMENT '隐患编号',
  hazard_level VARCHAR(32) NULL COMMENT '隐患等级',
  status VARCHAR(32) NULL COMMENT '状态',
  occurred_at DATETIME NULL COMMENT '发生时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_dw_hazard_site (tenant_id, site_id, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='隐患数据仓库事实';

CREATE TABLE IF NOT EXISTS dw_incident_fact (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  site_id BIGINT NULL COMMENT '站点ID',
  incident_no VARCHAR(64) NULL COMMENT '事件编号',
  incident_level VARCHAR(32) NULL COMMENT '事件等级',
  status VARCHAR(32) NULL COMMENT '状态',
  occurred_at DATETIME NULL COMMENT '发生时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_dw_incident_site (tenant_id, site_id, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件数据仓库事实';

CREATE TABLE IF NOT EXISTS dw_moc_fact (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  site_id BIGINT NULL COMMENT '站点ID',
  change_no VARCHAR(64) NULL COMMENT '变更编号',
  change_level VARCHAR(32) NULL COMMENT '变更等级',
  status VARCHAR(32) NULL COMMENT '状态',
  occurred_at DATETIME NULL COMMENT '发生时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_dw_moc_site (tenant_id, site_id, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MOC数据仓库事实';

CREATE TABLE IF NOT EXISTS dw_work_permit_fact (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  site_id BIGINT NULL COMMENT '站点ID',
  permit_no VARCHAR(64) NULL COMMENT '许可证编号',
  permit_type VARCHAR(64) NULL COMMENT '许可证类型',
  status VARCHAR(32) NULL COMMENT '状态',
  occurred_at DATETIME NULL COMMENT '发生时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_dw_permit_site (tenant_id, site_id, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业许可数据仓库事实';

-- ---------- INCIDENT 事故事件模块 (8 tables) ----------
CREATE TABLE IF NOT EXISTS incident_report (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  incident_no VARCHAR(64) NOT NULL COMMENT '事件编号',
  incident_type VARCHAR(32) NOT NULL COMMENT '事件类型',
  incident_level VARCHAR(32) NOT NULL COMMENT '事件等级',
  occurred_at DATETIME NOT NULL COMMENT '发生时间',
  area_id BIGINT NULL COMMENT '区域ID',
  equipment_id BIGINT NULL COMMENT '设备ID',
  source_type VARCHAR(32) NULL COMMENT '来源类型',
  source_biz_id BIGINT NULL COMMENT '来源业务ID',
  status VARCHAR(32) NOT NULL DEFAULT 'REPORTED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_incident_no (tenant_id, incident_no, deleted),
  KEY idx_incident_status (tenant_id, status, occurred_at),
  KEY idx_incident_area (tenant_id, area_id, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件报告';

CREATE TABLE IF NOT EXISTS incident_investigation (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  incident_id BIGINT NOT NULL COMMENT '事件ID',
  lead_user_id BIGINT NULL COMMENT '调查组长ID',
  scope_desc VARCHAR(1024) NULL COMMENT '调查范围',
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_incident_inv (tenant_id, incident_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件调查';

CREATE TABLE IF NOT EXISTS incident_timeline (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  incident_id BIGINT NOT NULL COMMENT '事件ID',
  event_at DATETIME NOT NULL COMMENT '事件时间',
  event_desc VARCHAR(1024) NOT NULL COMMENT '事件描述',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_incident_timeline (tenant_id, incident_id, event_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件时间线';

CREATE TABLE IF NOT EXISTS incident_root_cause (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  incident_id BIGINT NOT NULL COMMENT '事件ID',
  cause_type VARCHAR(32) NOT NULL COMMENT '原因类型',
  cause_desc VARCHAR(1024) NOT NULL COMMENT '原因描述',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_incident_cause (tenant_id, incident_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件根因';

CREATE TABLE IF NOT EXISTS incident_capa (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  incident_id BIGINT NOT NULL COMMENT '事件ID',
  capa_no VARCHAR(64) NOT NULL COMMENT 'CAPA编号',
  capa_type VARCHAR(32) NOT NULL COMMENT 'CAPA类型',
  owner_user_id BIGINT NULL COMMENT '负责人ID',
  due_at DATETIME NULL COMMENT '到期时间',
  verification_user_id BIGINT NULL COMMENT '验证人ID',
  evidence_file_id BIGINT NULL COMMENT '证据文件ID',
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_capa_no (tenant_id, capa_no, deleted),
  KEY idx_capa_incident (tenant_id, incident_id),
  KEY idx_capa_status (tenant_id, status, due_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CAPA纠正预防措施';

CREATE TABLE IF NOT EXISTS incident_evidence (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  incident_id BIGINT NOT NULL COMMENT '事件ID',
  evidence_type VARCHAR(32) NOT NULL COMMENT '证据类型',
  file_id BIGINT NULL COMMENT '文件ID',
  description VARCHAR(512) NULL COMMENT '描述',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_incident_evidence (tenant_id, incident_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件证据';

CREATE TABLE IF NOT EXISTS incident_interview (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  incident_id BIGINT NOT NULL COMMENT '事件ID',
  interviewee VARCHAR(128) NOT NULL COMMENT '受访人',
  summary VARCHAR(2048) NULL COMMENT '访谈摘要',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_incident_interview (tenant_id, incident_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件访谈记录';

CREATE TABLE IF NOT EXISTS lesson_learned (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  incident_id BIGINT NOT NULL COMMENT '事件ID',
  lesson_desc VARCHAR(2048) NOT NULL COMMENT '经验教训描述',
  action_type VARCHAR(32) NULL COMMENT '行动类型',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_lesson_incident (tenant_id, incident_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='经验教训库';

-- ---------- INTEGRATION 监管集成模块 (6 tables) ----------
CREATE TABLE IF NOT EXISTS reg_platform_config (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  platform_code VARCHAR(64) NOT NULL COMMENT '平台编码',
  platform_name VARCHAR(255) NOT NULL COMMENT '平台名称',
  base_url VARCHAR(512) NULL COMMENT '基础URL',
  auth_type VARCHAR(32) NULL COMMENT '认证类型',
  credential_ref VARCHAR(255) NULL COMMENT '凭据引用',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  remark VARCHAR(512) NULL COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_reg_platform_code (tenant_id, platform_code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管平台配置';

CREATE TABLE IF NOT EXISTS reg_code_mapping (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  platform_code VARCHAR(64) NOT NULL COMMENT '平台编码',
  mapping_type VARCHAR(32) NOT NULL COMMENT '映射类型',
  source_code VARCHAR(128) NOT NULL COMMENT '源编码',
  target_code VARCHAR(128) NOT NULL COMMENT '目标编码',
  description VARCHAR(512) NULL COMMENT '描述',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  UNIQUE KEY uk_reg_code_map (tenant_id, platform_code, mapping_type, source_code, deleted),
  KEY idx_reg_code_map_type (tenant_id, platform_code, mapping_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管编码映射';

CREATE TABLE IF NOT EXISTS reg_field_mapping (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  platform_code VARCHAR(64) NOT NULL COMMENT '平台编码',
  data_domain VARCHAR(64) NOT NULL COMMENT '数据域',
  source_field VARCHAR(128) NOT NULL COMMENT '源字段',
  target_field VARCHAR(128) NOT NULL COMMENT '目标字段',
  transform_rule VARCHAR(512) NULL COMMENT '转换规则',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识',
  PRIMARY KEY (id),
  KEY idx_reg_field_map (tenant_id, platform_code, data_domain)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管字段映射';

CREATE TABLE IF NOT EXISTS reg_report_task (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  task_no VARCHAR(64) NOT NULL COMMENT '任务编号',
  platform_code VARCHAR(64) NOT NULL COMMENT '平台编码',
  data_domain VARCHAR(64) NOT NULL COMMENT '数据域',
  trigger_type VARCHAR(32) NOT NULL COMMENT '触发类型',
  data_window_start DATETIME NULL COMMENT '数据窗口开始',
  data_window_end DATETIME NULL COMMENT '数据窗口结束',
  record_count INT NULL COMMENT '记录数',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  payload_digest VARCHAR(128) NULL COMMENT '数据摘要',
  executed_at DATETIME NULL COMMENT '执行时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_reg_task_no (tenant_id, task_no),
  KEY idx_reg_task_status (tenant_id, status, created_at),
  KEY idx_reg_task_platform (tenant_id, platform_code, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管上报任务';

CREATE TABLE IF NOT EXISTS reg_report_receipt (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  task_id BIGINT NOT NULL COMMENT '任务ID',
  success TINYINT NOT NULL DEFAULT 0 COMMENT '是否成功',
  platform_code VARCHAR(64) NULL COMMENT '平台编码',
  platform_message VARCHAR(1024) NULL COMMENT '平台返回消息',
  receipt_time DATETIME NULL COMMENT '回执时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_reg_receipt_task (tenant_id, task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管上报回执';

CREATE TABLE IF NOT EXISTS reg_retry_queue (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  task_id BIGINT NOT NULL COMMENT '任务ID',
  retry_count INT NOT NULL DEFAULT 0 COMMENT '已重试次数',
  max_retries INT NOT NULL DEFAULT 3 COMMENT '最大重试次数',
  next_retry_at DATETIME NULL COMMENT '下次重试时间',
  last_error VARCHAR(1024) NULL COMMENT '最近错误',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_reg_retry_task (tenant_id, task_id),
  KEY idx_reg_retry_next (tenant_id, status, next_retry_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管上报重试队列';

-- ---------- REPORT 报表模块 (3 tables) ----------
CREATE TABLE IF NOT EXISTS acceptance_test_case (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  case_code VARCHAR(64) NOT NULL COMMENT '用例编码',
  case_name VARCHAR(255) NOT NULL COMMENT '用例名称',
  module VARCHAR(64) NULL COMMENT '模块',
  scenario VARCHAR(512) NULL COMMENT '测试场景',
  expected_result VARCHAR(1024) NULL COMMENT '预期结果',
  priority VARCHAR(16) NULL COMMENT '优先级',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_acceptance_case_code (tenant_id, case_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验收测试用例';

CREATE TABLE IF NOT EXISTS acceptance_test_run (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  case_id BIGINT NOT NULL COMMENT '用例ID',
  run_no VARCHAR(64) NOT NULL COMMENT '运行编号',
  executor_name VARCHAR(128) NULL COMMENT '执行人',
  run_status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '运行状态',
  evidence_ref VARCHAR(512) NULL COMMENT '证据引用',
  remark VARCHAR(512) NULL COMMENT '备注',
  executed_at DATETIME NULL COMMENT '执行时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_acceptance_run_case (tenant_id, case_id),
  KEY idx_acceptance_run_status (tenant_id, run_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验收测试执行记录';

CREATE TABLE IF NOT EXISTS report_export_task (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  report_type VARCHAR(64) NOT NULL COMMENT '报表类型',
  export_format VARCHAR(16) NOT NULL DEFAULT 'XLSX' COMMENT '导出格式',
  query_params_json MEDIUMTEXT NULL COMMENT '查询参数JSON',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  file_path VARCHAR(512) NULL COMMENT '文件路径',
  error_message VARCHAR(1024) NULL COMMENT '错误信息',
  requested_by VARCHAR(128) NULL COMMENT '请求人',
  started_at DATETIME NULL COMMENT '开始时间',
  completed_at DATETIME NULL COMMENT '完成时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_report_export_type (tenant_id, report_type, status),
  KEY idx_report_export_status (tenant_id, status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表导出任务';
