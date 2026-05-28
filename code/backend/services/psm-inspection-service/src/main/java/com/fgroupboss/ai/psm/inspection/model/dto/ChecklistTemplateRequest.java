package com.fgroupboss.ai.psm.inspection.model.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChecklistTemplateRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotBlank(message = "templateCode is required")
    private String templateCode;
    @NotBlank(message = "templateName is required")
    private String templateName;
    private String category;
    private String remark;
    @Valid
    private List<ChecklistItemRequest> items;
}
