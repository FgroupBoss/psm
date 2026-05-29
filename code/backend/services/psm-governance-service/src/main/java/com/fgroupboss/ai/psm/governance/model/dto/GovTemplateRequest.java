package com.fgroupboss.ai.psm.governance.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 集团模板创建/更新请求。
 */
@Data
public class GovTemplateRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "templateCode is required")
    private String templateCode;

    @NotBlank(message = "templateName is required")
    private String templateName;

    @NotBlank(message = "templateType is required")
    private String templateType;

    private String content;
}
