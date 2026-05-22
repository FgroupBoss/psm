# psm-work-permit-service 数据脚本依赖

一期危险工作票依赖 `psm-master-data-service` 的租户、组织、用户、岗位、区域、装置和设备主数据。

部署顺序：

1. 执行 `psm-master-data-service/src/main/resources/db/schema.sql`
2. 执行 `psm-master-data-service/src/main/resources/db/data.sql`
3. 执行 `psm-iam-service/src/main/resources/db/schema.sql`
4. 再执行本服务后续工作票、审批、气体检测、措施确认、现场许可和归档脚本

工作票表仅保存主数据 ID 或编码引用，主数据状态校验由接口联调时完成。
