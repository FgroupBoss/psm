package com.fgroupboss.ai.psm.iam.model.vo;

import lombok.Data;

/**
 * 角色视图。
 */
@Data
public class RoleVO {

    private Long id;
    private Long tenantId;
    private String roleCode;
    private String roleName;
    private String roleType;
    private String description;
    private String status;
}
