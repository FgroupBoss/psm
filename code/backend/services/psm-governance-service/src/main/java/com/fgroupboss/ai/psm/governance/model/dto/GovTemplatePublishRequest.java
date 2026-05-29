package com.fgroupboss.ai.psm.governance.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 模板发布请求。
 */
@Data
public class GovTemplatePublishRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "versionNo is required")
    private String versionNo;

    private String content;
}
