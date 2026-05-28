package com.fgroupboss.ai.psm.dualprevention.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HazardReportVO {

    private Long id;
    private Long tenantId;
    private String hazardNo;
    private String hazardLevel;
    private String sourceType;
    private Long sourceBizId;
    private Long riskUnitId;
    private Long areaId;
    private String description;
    private LocalDateTime foundAt;
    private LocalDateTime rectificationDeadline;
    private String status;
    private Integer overdueFlag;
    private Long assigneeOrgId;
    private Long assigneeUserId;
    private Long contractorId;
}
