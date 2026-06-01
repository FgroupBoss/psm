package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class BlindPlateWorkDetailRequest {

    private Long tenantId;
    private Long blindPlateId;
    private String operationType;
    private String pipelineId;
    private String positionDescription;
    private String positionDiagramRef;
    private String mediumName;
    private String temperature;
    private String pressure;
    private String hazardJson;
    private String tagNo;
    private String spec;
    private String material;
}
