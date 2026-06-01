package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TempElectricDetailVO {

    private Long id;
    private Long workPermitId;
    private Long sourceId;
    private String voltage;
    private BigDecimal loadKw;
    private Boolean hazardousAreaFlag;
    private String planRef;
    private String ruleVersion;
    private Date validUntil;
}
