package com.fgroupboss.ai.psm.identity.iam.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 岗位保存请求。
 */
@Data
public class PostRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "postCode is required")
    private String postCode;

    @NotBlank(message = "postName is required")
    private String postName;

    private Long orgId;
    private String status;
}
