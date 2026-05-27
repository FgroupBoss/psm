-- 试点租户报警种子（批次 1 联调；批次 2 起由 ingest 写入）
insert into alarm_dedup_rule (id, tenant_id, rule_code, source_type, window_seconds, enabled)
values (1, 1, 'DEFAULT_GDS', 'GDS', 300, 1)
on duplicate key update window_seconds = values(window_seconds), updated_at = now();

insert into alarm_rule (id, tenant_id, rule_code, rule_name, rule_type, config_json, status)
values
  (1, 1, 'CONFIRM_TIMEOUT_L1', '一级报警确认超时', 'TIMEOUT', '{"level":"LEVEL_1","confirmMinutes":5}', 'ENABLED'),
  (2, 1, 'DISPOSE_TIMEOUT_L1', '一级报警处置超时', 'TIMEOUT', '{"level":"LEVEL_1","disposeMinutes":30}', 'ENABLED')
on duplicate key update rule_name = values(rule_name), updated_at = now();

insert into alarm_event (
  id, tenant_id, alarm_no, source_type, source_code, title, content, alarm_level, status,
  area_id, monitor_point_id, hazard_id, dedup_key, occurrence_count,
  first_occurred_at, last_occurred_at, deleted
)
values
  (1, 1, 'ALM-20260527-001', 'GDS', 'MP-010-H2', '储罐区可燃气体高报', 'H2 浓度超过阈值', 'LEVEL_1', 'NEW',
   1, 10, 1, 'GDS|MP-010-H2|LEVEL_1', 1, '2026-05-27 08:00:00', '2026-05-27 08:00:00', 0),
  (2, 1, 'ALM-20260527-002', 'GDS', 'MP-011-T', '反应器温度高报', '温度持续升高', 'LEVEL_2', 'CONFIRMED',
   1, 11, 1, 'GDS|MP-011-T|LEVEL_2', 2, '2026-05-27 07:30:00', '2026-05-27 08:10:00', 0),
  (3, 1, 'ALM-20260527-003', 'BUSINESS', 'RULE-AREA-01', '区域风险规则报警', '模拟业务规则触发', 'LEVEL_2', 'IN_PROGRESS',
   1, null, 2, 'BUSINESS|RULE-AREA-01|1', 1, '2026-05-27 06:00:00', '2026-05-27 06:00:00', 0),
  (4, 1, 'ALM-20260527-004', 'GDS', 'MP-012-P', '压力高高报', '已升级待处置', 'LEVEL_1', 'ESCALATED',
   1, 12, 1, 'GDS|MP-012-P|LEVEL_1', 3, '2026-05-27 05:00:00', '2026-05-27 05:45:00', 0),
  (5, 1, 'ALM-20260527-005', 'GDS', 'MP-013-L', '液位低低报误报', '仪表校验误报', 'LEVEL_3', 'FALSE_CLOSED',
   1, 13, 2, 'GDS|MP-013-L|LEVEL_3', 1, '2026-05-26 22:00:00', '2026-05-26 22:00:00', 0)
on duplicate key update title = values(title), status = values(status), updated_at = now();
