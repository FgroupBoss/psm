package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

@Data
public class TempElectricFacilityRequest {

    private Long tenantId;
    private String facilityType;
    private String facilityNo;
    private String protectionType;
    private String groundingResult;
    private String qrCode;
}
