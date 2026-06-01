package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TempElectricInspectionRequest {

    private Long tenantId;
    private Long facilityId;
    private String inspectionResult;
    private String issueDesc;
    private String rectification;
    private Date inspectedAt;
}
