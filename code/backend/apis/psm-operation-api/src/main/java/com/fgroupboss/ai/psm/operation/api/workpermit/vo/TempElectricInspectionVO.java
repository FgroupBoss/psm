package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TempElectricInspectionVO {

    private Long id;
    private Long facilityId;
    private String inspectionResult;
    private String issueDesc;
    private String rectification;
    private String inspectedBy;
    private Date inspectedAt;
}
