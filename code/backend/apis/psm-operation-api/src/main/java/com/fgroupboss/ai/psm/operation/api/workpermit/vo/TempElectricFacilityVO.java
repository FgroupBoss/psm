package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

@Data
public class TempElectricFacilityVO {

    private Long id;
    private String facilityType;
    private String facilityNo;
    private String protectionType;
    private String groundingResult;
    private String qrCode;
    private String status;
}
