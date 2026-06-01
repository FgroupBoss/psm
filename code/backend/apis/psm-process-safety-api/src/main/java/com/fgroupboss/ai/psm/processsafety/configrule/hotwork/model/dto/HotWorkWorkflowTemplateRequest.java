package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class HotWorkWorkflowTemplateRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "templateCode is required")
    private String templateCode;
    @NotBlank(message = "templateName is required")
    private String templateName;
    @NotBlank(message = "hotWorkLevel is required")
    private String hotWorkLevel;
    private String areaScopeType = "ALL";
    private List<Long> areaIds;
    private String remark;
    private List<HotWorkWorkflowNodeRequest> nodes;
}
