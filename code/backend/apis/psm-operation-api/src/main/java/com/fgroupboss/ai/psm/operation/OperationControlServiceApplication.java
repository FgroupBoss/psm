package com.fgroupboss.ai.psm.operation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 作业管控域服务 — 统一入口。
 * 合并了 work-permit + contractor + mobile-bff。
 * 子包结构：
 * <ul>
 *   <li>operation.api         — 对外 DTO/VO 契约</li>
 *   <li>operation.workpermit  — 作业许可/SIMOPS</li>
 *   <li>operation.contractor  — 承包商管理</li>
 *   <li>operation.mobile      — 移动端聚合</li>
 *   <li>operation.config      — 多数据源配置</li>
 * </ul>
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fgroupboss.ai.psm.operation",
        "com.fgroupboss.ai.psm.common"
})
public class OperationControlServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperationControlServiceApplication.class, args);
    }
}
