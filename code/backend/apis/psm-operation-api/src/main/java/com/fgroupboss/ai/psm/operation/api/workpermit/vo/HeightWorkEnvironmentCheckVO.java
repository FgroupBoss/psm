package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class HeightWorkEnvironmentCheckVO {

    private Long id;
    private String checkStage;
    private String windLevel;
    private String weatherType;
    private BigDecimal temperatureC;
    private BigDecimal visibilityM;
    private BigDecimal illuminationLux;
    private String groundCondition;
    private Boolean powerProximityFlag;
    private String dataSource;
    private Date sourceSampledAt;
    private String checkResult;
    private String reviewReason;
    private String checkedBy;
    private Date checkedAt;
}
