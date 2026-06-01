package com.fgroupboss.ai.psm.identity.iam.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 菜单与权限资源保存请求。
 */
@Data
public class MenuResourceRequest {

    private Long tenantId;
    private Long parentId;

    @NotBlank(message = "resourceType is required")
    private String resourceType;

    @NotBlank(message = "resourceCode is required")
    private String resourceCode;

    @NotBlank(message = "resourceName is required")
    private String resourceName;

    private String routePath;
    private String apiPath;
    private String httpMethod;
    private Integer sortOrder;
    @NotNull(message = "visible is required")
    private Boolean visible;
    private String status;
}
