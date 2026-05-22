# psm-mobile-bff 数据脚本依赖

移动端 BFF 不直接拥有主数据表。

部署顺序：

1. 执行 `psm-master-data-service/src/main/resources/db/schema.sql`
2. 执行 `psm-iam-service/src/main/resources/db/schema.sql`
3. 执行业务服务脚本

移动端待办、签到、拍照、电子签名等数据应落在对应业务服务或文件服务。
