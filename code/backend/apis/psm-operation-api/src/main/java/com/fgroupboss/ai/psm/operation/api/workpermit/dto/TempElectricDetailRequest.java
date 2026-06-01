package com.fgroupboss.ai.psm.operation.api.workpermit.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TempElectricDetailRequest {

    private Long tenantId;
    private Long sourceId;
    private String voltage;
    private BigDecimal loadKw;
    private Boolean hazardousAreaFlag;
    private String planRef;
}
