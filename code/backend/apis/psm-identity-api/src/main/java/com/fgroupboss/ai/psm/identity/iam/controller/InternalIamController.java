package com.fgroupboss.ai.psm.identity.iam.controller;

import com.fgroupboss.ai.psm.common.LoginContext;
import com.fgroupboss.ai.psm.common.UserContext;
import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.identity.iam.model.vo.UserPermissionSummaryVO;
import com.fgroupboss.ai.psm.identity.iam.service.IamAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InternalIam 模块 HTTP API。
 * <p>基础路径：{@code /internal/iam}</p>
 * <p>返回体均为 {@link com.fgroupboss.ai.psm.common.ResponseVO}；写操作需透传租户与操作人上下文。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/iam")
public class InternalIamController {

    private final IamAdminService service;

    /**
     * 查询permissions。
     * <p>HTTP GET {@code /internal/iam/users/{userId}/permissions}</p>
     * <p>所有查询与变更均按租户隔离。写操作从请求头解析操作人并写入审计字段。</p>
     * @param userId 当前操作人用户 ID（请求头透传）
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/users/{userId}/permissions")
    public ResponseVO<UserPermissionSummaryVO> permissions(@LoginContext UserContext loginContext,
                                        @PathVariable Long userId) {
        return ResponseVO.success(service.userPermissions(loginContext.getTenantId(), userId));
    }

    /**
     * 查询permission version。
     * <p>HTTP GET {@code /internal/iam/users/{userId}/permission-version}</p>
     * <p>所有查询与变更均按租户隔离。写操作从请求头解析操作人并写入审计字段。</p>
     * @param userId 当前操作人用户 ID（请求头透传）
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/users/{userId}/permission-version")
    public ResponseVO<Long> permissionVersion(@LoginContext UserContext loginContext,
                                        @PathVariable Long userId) {
        return ResponseVO.success(service.userPermissions(loginContext.getTenantId(), userId).getPermissionVersion());
    }

    /**
     * 查询data scope。
     * <p>HTTP GET {@code /internal/iam/users/{userId}/data-scope}</p>
     * <p>所有查询与变更均按租户隔离。写操作从请求头解析操作人并写入审计字段。</p>
     * @param userId 当前操作人用户 ID（请求头透传）
     * @param tenantId 租户 ID，多租户隔离必填
     * @return 业务数据对象，统一封装为 {@link com.fgroupboss.ai.psm.common.ResponseVO}
     */
    @GetMapping("/users/{userId}/data-scope")
    public ResponseVO<UserPermissionSummaryVO> dataScope(@LoginContext UserContext loginContext,
                                        @PathVariable Long userId) {
        return ResponseVO.success(service.userPermissions(loginContext.getTenantId(), userId));
    }
}
