package com.fgroupboss.ai.psm.processsafety.configrule.hotwork.model.dto;

import lombok.Data;

@Data
public class HotWorkWorkflowQueryDTO {

    private Long tenantId;
    private String hotWorkLevel;
    private Long areaId;
    private String status = "PUBLISHED";
}
