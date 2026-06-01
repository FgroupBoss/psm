package com.fgroupboss.ai.psm.processsafety.pha.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LopaCalculateResultVO {
    private Long scenarioId;
    private BigDecimal mitigatedFrequency;
    private String silRecommendation;
    private String calculationVersion;
}
