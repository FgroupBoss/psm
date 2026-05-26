-- 第 2 迭代批次 3：证书类型与作业类型字典种子（tenant_id=1）
insert into config_item (id, tenant_id, config_type, config_code, config_name, version_no, status, biz_scene, content_json, remark)
values
  (101, 1, 'DICTIONARY', 'CERT_TYPE', '证书类型', 1, 'PUBLISHED', 'CONTRACTOR',
   '{"items":[{"code":"SPECIAL_WELDER","name":"特种焊工证"},{"code":"SPECIAL_HEIGHT","name":"高处作业证"},{"code":"SPECIAL_ELECTRIC","name":"电工证"}]}',
   '承包商人员证书类型'),
  (102, 1, 'DICTIONARY', 'WORK_PERMIT_TYPE', '作业类型', 1, 'PUBLISHED', 'WORK_PERMIT',
   '{"items":[{"code":"HOT_WORK","name":"动火作业"},{"code":"HEIGHT_WORK","name":"高处作业"},{"code":"CONFINED_SPACE","name":"受限空间"}]}',
   '作业票类型，供 eligibility-check workType 关联')
on duplicate key update config_name = values(config_name), content_json = values(content_json), status = values(status), updated_at = now();
