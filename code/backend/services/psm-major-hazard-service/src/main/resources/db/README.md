# psm-major-hazard-service 数据脚本依赖

一期重大危险源依赖 `psm-master-data-service` 的区域、装置、设备、监测点位和用户主数据。

部署顺序：

1. 执行 `psm-master-data-service/src/main/resources/db/schema.sql`
2. 执行 `psm-master-data-service/src/main/resources/db/data.sql`
3. 再执行本服务后续重大危险源档案、分级、责任人、点位绑定和附件关系脚本

本服务只保存重大危险源业务关系，不重复保存基础区域、设备和点位主表。
