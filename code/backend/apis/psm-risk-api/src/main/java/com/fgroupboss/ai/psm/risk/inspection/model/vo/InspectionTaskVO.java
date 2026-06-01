package com.fgroupboss.ai.psm.risk.inspection.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InspectionTaskVO {

    private Long id;
    private Long tenantId;
    private String taskNo;
    private Long planId;
    private Long routeId;
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private Long executorId;
    private String status;
    private BigDecimal completionRate;
    private Integer abnormalCount;
    private List<TaskItemVO> items;
}
