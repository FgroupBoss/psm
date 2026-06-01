package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class HeightWorkHazardFactorRequest {

    private Long tenantId;
    private String factorCode;
    private String factorName;
    private String hitSource;
    private String controlMeasure;
    private String attachmentRef;
}
