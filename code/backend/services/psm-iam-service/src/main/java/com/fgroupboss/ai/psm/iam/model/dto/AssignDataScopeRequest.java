package com.fgroupboss.ai.psm.iam.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色数据权限分配请求。
 */
@Data
public class AssignDataScopeRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "scopeType is required")
    private String scopeType;

    private List<Long> orgIds;
    private List<Long> areaIds;
    private List<Long> unitIds;
    private Boolean includeChildren;
}
