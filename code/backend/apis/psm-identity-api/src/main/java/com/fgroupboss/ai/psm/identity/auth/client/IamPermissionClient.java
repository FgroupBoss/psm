package com.fgroupboss.ai.psm.identity.auth.client;

import com.fgroupboss.ai.psm.identity.iam.service.IamAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * IAM 权限版本查询客户端 — 同域内直接调用，不再经过 HTTP。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IamPermissionClient {

    private static final long DEFAULT_PERMISSION_VERSION = 1L;

    private final IamAdminService iamAdminService;

    /**
     * 查询用户当前权限版本号。
     * IAM 查询失败时返回默认版本 {@value #DEFAULT_PERMISSION_VERSION}，不阻断认证流程。
     */
    public long currentPermissionVersion(Long tenantId, Long userId) {
        try {
            return iamAdminService.userPermissions(tenantId, userId).getPermissionVersion();
        } catch (Exception e) {
            log.warn("iam permission version unavailable, tenantId={}, userId={}", tenantId, userId, e);
            return DEFAULT_PERMISSION_VERSION;
        }
    }
}
