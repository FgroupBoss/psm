package com.fgroupboss.ai.psm.iam.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 组织保存请求。
 */
@Data
public class OrgRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    private Long parentId;

    @NotBlank(message = "orgCode is required")
    private String orgCode;

    @NotBlank(message = "orgName is required")
    private String orgName;

    @NotBlank(message = "orgType is required")
    private String orgType;

    private Integer sortOrder;
    private String status;
}
