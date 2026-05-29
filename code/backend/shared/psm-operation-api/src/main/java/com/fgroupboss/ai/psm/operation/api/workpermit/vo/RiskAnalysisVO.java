package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RiskAnalysisVO {

    private Long id;
    private String hazardDesc;
    private String controlMeasure;
    private String riskLevel;
    private String analystName;
    private Date analyzedAt;
}

