-- 试点种子：2 条危险源（1 已发布绑区域，1 草稿；area_id=1 对齐主数据罐区）
insert into major_hazard (id, tenant_id, hazard_no, name, hazard_type, level, area_id, unit_id, material, design_capacity, status, published_at)
values
  (1, 1, 'HZ-001', '罐区A一级重大危险源', 'TANK_FARM', 'LEVEL_1', 1, null, '液化烃', '5000m3', 'PUBLISHED', '2025-06-01 10:00:00'),
  (2, 1, 'HZ-002', '装卸区二级重大危险源', 'LOADING', 'LEVEL_2', null, null, '汽油', '2000m3', 'DRAFT', null)
on duplicate key update name = values(name), status = values(status), published_at = values(published_at), updated_at = now();

insert into major_hazard_responsibility (id, tenant_id, hazard_id, responsibility_type, person_name, person_phone, sort_no)
values
  (1, 1, 1, 'PRIMARY', '张总', '139****2001', 1),
  (2, 1, 1, 'TECHNICAL', '李工', '139****2002', 2),
  (3, 1, 1, 'OPERATION', '王班长', '139****2003', 3)
on duplicate key update person_name = values(person_name), updated_at = now();

insert into major_hazard_point_rel (id, tenant_id, hazard_id, monitor_point_id, point_code, point_name)
values
  (1, 1, 1, 1, 'MP-001', 'V101可燃气体')
on duplicate key update point_name = values(point_name);
