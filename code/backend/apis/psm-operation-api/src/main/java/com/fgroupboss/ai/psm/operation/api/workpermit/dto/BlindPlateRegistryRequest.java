package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class BlindPlateRegistryRequest {

    private Long tenantId;
    private String blindPlateNo;
    private String pipelineId;
    private String position;
    private String diagramRef;
    private String spec;
    private String material;
    private String tagNo;
    private String status;
}
