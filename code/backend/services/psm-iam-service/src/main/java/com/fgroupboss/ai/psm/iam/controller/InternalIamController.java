package com.fgroupboss.ai.psm.iam.controller;

import com.fgroupboss.ai.psm.common.ResponseVO;
import com.fgroupboss.ai.psm.iam.model.vo.UserPermissionSummaryVO;
import com.fgroupboss.ai.psm.iam.service.IamAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * IAM 内部查询接口，供网关和业务服务做鉴权判断。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/iam")
public class InternalIamController {

    private final IamAdminService service;

    @GetMapping("/users/{userId}/permissions")
    public ResponseVO<UserPermissionSummaryVO> permissions(@PathVariable Long userId, @RequestParam Long tenantId) {
        return ResponseVO.success(service.userPermissions(tenantId, userId));
    }

    @GetMapping("/users/{userId}/permission-version")
    public ResponseVO<Long> permissionVersion(@PathVariable Long userId, @RequestParam Long tenantId) {
        return ResponseVO.success(service.userPermissions(tenantId, userId).getPermissionVersion());
    }

    @GetMapping("/users/{userId}/data-scope")
    public ResponseVO<UserPermissionSummaryVO> dataScope(@PathVariable Long userId, @RequestParam Long tenantId) {
        return ResponseVO.success(service.userPermissions(tenantId, userId));
    }
}
