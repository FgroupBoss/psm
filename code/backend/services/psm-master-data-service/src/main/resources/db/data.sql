insert into master_data_item(tenant_id, category, item_code, item_name, parent_id, item_type, status, attributes)
values
(1, 'tenant', 'DEMO', '试点租户', null, 'PILOT', 'ENABLED', json_object('dataIsolationMode', 'TENANT_ID')),
(1, 'org', 'ORG-GROUP', '试点集团', null, 'GROUP', 'ENABLED', json_object()),
(1, 'org', 'ORG-SITE-001', '试点厂区', null, 'SITE', 'ENABLED', json_object('parentCode', 'ORG-GROUP')),
(1, 'post', 'POST-SAFETY-MANAGER', '安全管理员', null, 'SAFETY', 'ENABLED', json_object()),
(1, 'user', 'admin', '系统管理员', null, 'LOCAL', 'ENABLED', json_object('username', 'admin', 'mobile', '')),
(1, 'area', 'AREA-001', '罐区一', null, 'TANK_FARM', 'ENABLED', json_object('riskLevel', 'HIGH', 'majorHazardFlag', true)),
(1, 'unit', 'UNIT-001', '储运装置', null, 'STORAGE', 'ENABLED', json_object('areaCode', 'AREA-001')),
(1, 'equipment', 'EQ-001', 'V101储罐', null, 'TANK', 'ENABLED', json_object('areaCode', 'AREA-001', 'unitCode', 'UNIT-001')),
(1, 'monitor_point', 'MP-001', 'V101可燃气体', null, 'GAS', 'ENABLED', json_object('sourceSystem', 'DCS', 'sourceTag', 'DCS.GAS.V101', 'unit', '%LEL', 'high', 20, 'highHigh', 40)),
(1, 'role', 'ROLE_TENANT_ADMIN', '租户管理员', null, 'TENANT_ADMIN', 'ENABLED', json_object()),
(1, 'menu', 'MENU_MASTER_DATA', '基础主数据', null, 'MENU', 'ENABLED', json_object('path', '/master-data')),
(1, 'permission', 'master-data:create', '基础主数据新增', null, 'API', 'ENABLED', json_object('method', 'POST', 'path', '/api/master-data/{type}')),
(1, 'data_scope', 'SCOPE_TENANT_ALL', '全租户数据', null, 'TENANT_ALL', 'ENABLED', json_object())
on duplicate key update item_name=values(item_name), updated_at=now();
