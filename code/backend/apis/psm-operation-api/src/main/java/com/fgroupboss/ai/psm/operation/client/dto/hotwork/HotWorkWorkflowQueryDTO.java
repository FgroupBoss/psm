package com.fgroupboss.ai.psm.operation.client.dto.hotwork;

import lombok.Data;

@Data
public class HotWorkWorkflowQueryDTO {

    private Long tenantId;
    private String hotWorkLevel;
    private Long areaId;
    private String status = "PUBLISHED";
}
