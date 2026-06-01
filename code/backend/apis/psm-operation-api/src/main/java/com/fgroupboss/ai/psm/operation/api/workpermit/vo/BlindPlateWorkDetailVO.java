package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BlindPlateWorkDetailVO {

    private Long id;
    private Long workPermitId;
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
    private Boolean actionConfirmed;
    private String ruleVersion;
}
