-- 试点租户、管理员、平台菜单与角色权限种子（需与 auth 库 admin 用户 auth_user_id=1 对齐）
insert into sys_tenant (id, tenant_code, tenant_name, tenant_type, status, data_isolation_mode)
values (1, 'pilot', '试点租户', 'PILOT', 'ENABLED', 'TENANT')
on duplicate key update tenant_name = values(tenant_name), updated_at = now();

insert into sys_user (id, tenant_id, auth_user_id, username, display_name, account_type, status, permission_version)
values (1, 1, 1, 'admin', '系统管理员', 'LOCAL', 'ENABLED', 1)
on duplicate key update display_name = values(display_name), auth_user_id = values(auth_user_id), updated_at = now();

insert into sys_role (id, tenant_id, role_code, role_name, role_type, status)
values (1, 1, 'PLATFORM_ADMIN', '平台管理员', 'SYSTEM', 'ENABLED')
on duplicate key update role_name = values(role_name), updated_at = now();

insert into sys_user_role (tenant_id, user_id, role_id)
select 1, 1, 1 from dual
where not exists (select 1 from sys_user_role where tenant_id = 1 and user_id = 1 and role_id = 1);

insert into sys_menu (id, tenant_id, parent_id, resource_type, resource_code, resource_name, route_path, sort_order, visible, status)
values
  (1, 0, null, 'MENU', 'MENU_ROOT_MD', '基础主数据', null, 10, 1, 'ENABLED'),
  (2, 0, 1, 'MENU', 'MENU_MD_AREAS', '区域台账', 'master-data:areas', 11, 1, 'ENABLED'),
  (3, 0, 1, 'MENU', 'MENU_MD_UNITS', '装置台账', 'master-data:units', 12, 1, 'ENABLED'),
  (4, 0, 1, 'MENU', 'MENU_MD_EQUIPMENTS', '设备台账', 'master-data:equipments', 13, 1, 'ENABLED'),
  (5, 0, 1, 'MENU', 'MENU_MD_POINTS', '监测点位', 'master-data:monitor-points', 14, 1, 'ENABLED'),
  (10, 0, null, 'MENU', 'MENU_ROOT_IAM', '权限管理', null, 20, 1, 'ENABLED'),
  (11, 0, 10, 'MENU', 'MENU_IAM_ORGS', '组织管理', 'iam:orgs', 21, 1, 'ENABLED'),
  (12, 0, 10, 'MENU', 'MENU_IAM_USERS', '用户管理', 'iam:users', 22, 1, 'ENABLED'),
  (13, 0, 10, 'MENU', 'MENU_IAM_ROLES', '角色管理', 'iam:roles', 23, 1, 'ENABLED'),
  (14, 0, 10, 'MENU', 'MENU_IAM_MENUS', '菜单资源', 'iam:menus', 24, 1, 'ENABLED'),
  (20, 0, null, 'MENU', 'MENU_CONFIG', '系统配置与规则', 'config', 30, 1, 'ENABLED'),
  (21, 0, null, 'MENU', 'MENU_AUDIT', '权限审计', 'audit', 40, 1, 'ENABLED'),
  (30, 0, null, 'MENU', 'MENU_ROOT_CONTRACTOR', '承包商管理', null, 50, 1, 'ENABLED'),
  (31, 0, 30, 'MENU', 'MENU_CTR_COMPANIES', '承包商单位', 'contractor:companies', 51, 1, 'ENABLED'),
  (32, 0, 30, 'MENU', 'MENU_CTR_WORKERS', '承包商人员', 'contractor:workers', 52, 1, 'ENABLED'),
  (40, 0, null, 'MENU', 'MENU_ROOT_HAZARD', '重大危险源', null, 60, 1, 'ENABLED'),
  (41, 0, 40, 'MENU', 'MENU_HAZ_LEDGER', '危险源台账', 'hazard:ledger', 61, 1, 'ENABLED'),
  (50, 0, null, 'MENU', 'MENU_ROOT_ALARM', '报警中心', null, 70, 1, 'ENABLED'),
  (51, 0, 50, 'MENU', 'MENU_ALM_LIST', '实时报警', 'alarm:list', 71, 1, 'ENABLED'),
  (60, 0, null, 'MENU', 'MENU_ROOT_WKP', '危险工作票', null, 80, 1, 'ENABLED'),
  (61, 0, 60, 'MENU', 'MENU_WKP_LIST', '作业票列表', 'work-permit:list', 81, 1, 'ENABLED'),
  (70, 0, null, 'MENU', 'MENU_ROOT_REPORT', '报表大屏', null, 90, 1, 'ENABLED'),
  (71, 0, 70, 'MENU', 'MENU_RPT_OVERVIEW', '报表总览', 'report:overview', 91, 1, 'ENABLED'),
  (72, 0, 70, 'MENU', 'MENU_RPT_DASHBOARD', '态势大屏', 'report:dashboard', 92, 1, 'ENABLED'),
  (73, 0, 70, 'MENU', 'MENU_RPT_ACCEPTANCE', '上线验收', 'report:acceptance', 93, 1, 'ENABLED')
on duplicate key update resource_name = values(resource_name), route_path = values(route_path), updated_at = now();

insert into sys_role_permission (tenant_id, role_id, resource_id, permission_code)
select 1, 1, m.id, m.resource_code
from sys_menu m
where m.tenant_id = 0
  and m.resource_type = 'MENU'
  and not exists (
    select 1 from sys_role_permission rp
    where rp.tenant_id = 1 and rp.role_id = 1 and rp.resource_id = m.id
  );
