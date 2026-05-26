package com.fgroupboss.ai.psm.iam.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户权限摘要，供前端、网关和业务服务做权限判断。
 */
@Data
public class UserPermissionSummaryVO {

    private Long tenantId;
    private Long userId;
    private String username;
    private Long permissionVersion;
    private List<String> permissionCodes = new ArrayList<String>();
    private List<MenuTreeVO> menus = new ArrayList<MenuTreeVO>();
    private List<DataScopeVO> dataScopes = new ArrayList<DataScopeVO>();
}
