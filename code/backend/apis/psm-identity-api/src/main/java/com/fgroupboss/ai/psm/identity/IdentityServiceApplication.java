package com.fgroupboss.ai.psm.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 身份与接入域服务 — 统一入口。
 * 合并了 gateway + auth + iam + audit + file + notification + master-data。
 * 子包结构：
 * <ul>
 *   <li>identity.api          — 对外 DTO 契约</li>
 *   <li>identity.gateway      — API 网关路由代理</li>
 *   <li>identity.auth         — 登录、Token、SSO</li>
 *   <li>identity.iam          — 用户、组织、角色、权限</li>
 *   <li>identity.audit        — 审计日志</li>
 *   <li>identity.file         — 文件存储</li>
 *   <li>identity.notification — 消息通知</li>
 *   <li>identity.masterdata   — 基础数据（区域/设备/单元）</li>
 *   <li>identity.config       — 多数据源、Redis、线程池配置</li>
 * </ul>
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.fgroupboss.ai.psm.identity",
        "com.fgroupboss.ai.psm.common"
})
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }
}
