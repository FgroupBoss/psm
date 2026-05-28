package com.fgroupboss.ai.psm.inspection.model.vo;

import lombok.Data;

@Data
public class InspectionPlanVO {

    private Long id;
    private Long tenantId;
    private String planCode;
    private String planName;
    private Long routeId;
    private String cycleType;
    private String cronExpr;
    private Long teamId;
    private String teamName;
    private Long defaultExecutorId;
    private Long majorHazardId;
    private Integer enabled;
}
