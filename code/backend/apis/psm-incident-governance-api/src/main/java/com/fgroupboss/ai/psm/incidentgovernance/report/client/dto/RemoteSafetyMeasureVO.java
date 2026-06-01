package com.fgroupboss.ai.psm.incidentgovernance.report.client.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RemoteSafetyMeasureVO {

    private Long id;
    private String measureCode;
    private String measureName;
    private Boolean requiredFlag;
    private String confirmStatus;
    private Date confirmAt;
}
