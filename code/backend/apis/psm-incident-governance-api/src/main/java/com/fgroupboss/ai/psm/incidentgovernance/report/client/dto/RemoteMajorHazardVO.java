package com.fgroupboss.ai.psm.incidentgovernance.report.client.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RemoteMajorHazardVO {

    private Long id;
    private Long tenantId;
    private String hazardNo;
    private String name;
    private String hazardType;
    private String level;
    private Long areaId;
    private String status;
    private LocalDateTime publishedAt;
}
