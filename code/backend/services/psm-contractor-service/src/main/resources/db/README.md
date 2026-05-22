# psm-contractor-service 数据脚本依赖

一期承包商准入依赖 `psm-master-data-service` 的租户、组织、用户、岗位和区域主数据。

部署顺序：

1. 执行 `psm-master-data-service/src/main/resources/db/schema.sql`
2. 执行 `psm-master-data-service/src/main/resources/db/data.sql`
3. 再执行本服务后续承包商单位、人员、证书、培训、违章和黑名单脚本

本服务不得重复创建 `master_data_item`、`sys_user_identity`、`audit_change_log` 等平台共享表。
