-- 第 2 迭代批次 3：证书类型与作业类型字典种子（tenant_id=1）
insert into config_item (id, tenant_id, config_type, config_code, config_name, version_no, status, biz_scene, content_json, remark)
values
  (101, 1, 'DICTIONARY', 'CERT_TYPE', '证书类型', 1, 'PUBLISHED', 'CONTRACTOR',
   '{"items":[{"code":"SPECIAL_WELDER","name":"特种焊工证"},{"code":"SPECIAL_HEIGHT","name":"高处作业证"},{"code":"SPECIAL_ELECTRIC","name":"电工证"}]}',
   '承包商人员证书类型'),
  (102, 1, 'DICTIONARY', 'WORK_PERMIT_TYPE', '作业类型', 1, 'PUBLISHED', 'WORK_PERMIT',
   '{"items":[{"code":"HOT_WORK","name":"动火作业"},{"code":"HEIGHT_WORK","name":"高处作业"},{"code":"CONFINED_SPACE","name":"受限空间"}]}',
   '作业票类型，供 eligibility-check workType 关联'),
  (103, 1, 'DICTIONARY', 'HAZARD_LEVEL', '危险源等级', 1, 'PUBLISHED', 'MAJOR_HAZARD',
   '{"items":[{"code":"LEVEL_1","name":"一级"},{"code":"LEVEL_2","name":"二级"},{"code":"LEVEL_3","name":"三级"},{"code":"LEVEL_4","name":"四级"}]}',
   '重大危险源分级'),
  (104, 1, 'DICTIONARY', 'HAZARD_MEDIUM_TYPE', '危险源介质类型', 1, 'PUBLISHED', 'MAJOR_HAZARD',
   '{"items":[{"code":"LIQUEFIED_HYDROCARBON","name":"液化烃"},{"code":"GASOLINE","name":"汽油"},{"code":"TOXIC_GAS","name":"有毒气体"}]}',
   '重大危险源主要介质类型'),
  (105, 1, 'DICTIONARY', 'ALARM_LEVEL', '报警等级', 1, 'PUBLISHED', 'ALARM',
   '{"items":[{"code":"LEVEL_1","name":"一级"},{"code":"LEVEL_2","name":"二级"},{"code":"LEVEL_3","name":"三级"},{"code":"LEVEL_4","name":"四级"}]}',
   '报警中心等级字典'),
  (106, 1, 'DICTIONARY', 'ALARM_SOURCE', '报警来源', 1, 'PUBLISHED', 'ALARM',
   '{"items":[{"code":"GDS","name":"GDS监测"},{"code":"BUSINESS","name":"业务规则"},{"code":"MANUAL","name":"人工录入"}]}',
   '报警来源类型字典')
on duplicate key update config_name = values(config_name), content_json = values(content_json), status = values(status), updated_at = now();
