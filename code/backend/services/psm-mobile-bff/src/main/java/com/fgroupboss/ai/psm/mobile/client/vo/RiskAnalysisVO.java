package com.fgroupboss.ai.psm.mobile.client.vo;

import lombok.Data;

@Data
public class RiskAnalysisVO {

    private Long id;
    private String hazardDesc;
    private String controlMeasure;
    private String riskLevel;
}
