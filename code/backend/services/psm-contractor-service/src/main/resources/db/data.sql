-- 试点种子：2 家单位、5 名人员（tenant_id=1，与主数据试点租户对齐）
insert into contractor_company (id, tenant_id, company_code, company_name, contact_name, contact_phone, business_scope, status, blacklist_flag)
values
  (1, 1, 'CTR-001', '华东维保工程有限公司', '张经理', '138****0001', '设备维保、管道检修', 'APPROVED', 0),
  (2, 1, 'CTR-002', '南疆安装劳务队', '李队长', '138****0002', '钢结构安装', 'PENDING_REVIEW', 0)
on duplicate key update company_name = values(company_name), status = values(status), updated_at = now();

insert into contractor_qualification (id, tenant_id, company_id, qual_type, qual_name, qual_no, valid_from, valid_to, core_flag, status)
values
  (1, 1, 1, 'BUSINESS_LICENSE', '营业执照', '91310000MA1XXXX001', '2020-01-01', '2030-12-31', 1, 'ENABLED'),
  (2, 1, 2, 'BUSINESS_LICENSE', '营业执照', '91310000MA1XXXX002', '2021-06-01', '2026-05-31', 1, 'ENABLED')
on duplicate key update qual_name = values(qual_name), valid_to = values(valid_to), updated_at = now();

insert into contractor_worker (id, tenant_id, company_id, worker_code, name, phone_masked, trade_type, access_status, training_status, certificate_status, status)
values
  (1, 1, 1, 'W-001', '王强', '138****1001', 'WELDER', 'APPROVED', 'VALID', 'VALID', 'ENABLED'),
  (2, 1, 1, 'W-002', '赵敏', '138****1002', 'ELECTRICIAN', 'APPROVED', 'VALID', 'VALID', 'ENABLED'),
  (3, 1, 1, 'W-003', '刘洋', '138****1003', 'RIGGER', 'APPROVED', 'VALID', 'EXPIRING', 'ENABLED'),
  (4, 1, 2, 'W-004', '陈刚', '138****1004', 'WELDER', 'PENDING_REVIEW', 'VALID', 'VALID', 'ENABLED'),
  (5, 1, 2, 'W-005', '周婷', '138****1005', 'GENERAL', 'INCOMPLETE', 'INVALID', 'MISSING', 'ENABLED')
on duplicate key update name = values(name), access_status = values(access_status), updated_at = now();

insert into worker_certificate (id, tenant_id, worker_id, cert_type, cert_no, valid_from, valid_to, status)
values
  (1, 1, 1, 'SPECIAL_WELDER', 'SW-2024-001', '2024-01-01', '2027-12-31', 'ENABLED'),
  (2, 1, 3, 'SPECIAL_HEIGHT', 'SH-2023-009', '2023-01-01', '2025-06-30', 'ENABLED')
on duplicate key update valid_to = values(valid_to), updated_at = now();

insert into worker_training_record (id, tenant_id, worker_id, training_name, training_result, valid_from, valid_to)
values
  (1, 1, 1, '入厂安全培训', 'PASSED', '2025-01-01', '2026-12-31'),
  (2, 1, 5, '入厂安全培训', 'FAILED', '2025-01-01', '2025-12-31')
on duplicate key update training_result = values(training_result), updated_at = now();
