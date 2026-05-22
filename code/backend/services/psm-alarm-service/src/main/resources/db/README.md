# psm-alarm-service 数据脚本依赖

一期报警中心依赖 `psm-master-data-service` 的区域、设备、监测点位和用户主数据。

部署顺序：

1. 执行 `psm-master-data-service/src/main/resources/db/schema.sql`
2. 执行 `psm-master-data-service/src/main/resources/db/data.sql`
3. 再执行本服务后续报警事件、确认、派发、处置、升级和关闭脚本

报警来源点位通过主数据 `monitor_point` 编码关联，避免跨服务重复维护点位。
