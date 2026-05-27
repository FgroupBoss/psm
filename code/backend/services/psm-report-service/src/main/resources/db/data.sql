-- M08 UAT 验收用例种子（试点租户 1）
insert into acceptance_test_case (id, tenant_id, case_code, case_name, module, scenario, expected_result, priority, status)
values
  (1, 1, 'UAT-WP-001', '动火作业正常闭环', 'WORK_PERMIT',
   '创建、审批、气体检测、许可、监护、验收、归档全流程',
   '作业票状态终态为 CLOSED，关键节点有审计与留痕', 'P1', 'ACTIVE'),
  (2, 1, 'UAT-WP-002', '受限空间闭环', 'WORK_PERMIT',
   '人员登记、气体检测、措施确认、监护记录完整',
   '受限空间票各环节记录齐全且可下钻明细', 'P1', 'ACTIVE'),
  (3, 1, 'UAT-CT-001', '证书过期拦截', 'CONTRACTOR',
   '承包商人员证书过期时尝试加入作业',
   '系统拒绝加入并给出证书过期原因', 'P1', 'ACTIVE'),
  (4, 1, 'UAT-ALM-001', '报警处置闭环', 'ALARM',
   '确认、派发、处置、升级、关闭',
   '报警状态终态为 CLOSED 或 FALSE_CLOSED，动作链完整', 'P1', 'ACTIVE'),
  (5, 1, 'UAT-AUD-001', '审计追溯', 'AUDIT',
   '关键操作（审批、许可、报警关闭）执行后查询审计',
   '可追溯到操作人、时间、终端与变更内容', 'P2', 'ACTIVE')
on duplicate key update case_name = values(case_name), scenario = values(scenario),
  expected_result = values(expected_result), updated_at = now();
