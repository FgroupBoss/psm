package com.fgroupboss.ai.psm.location.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class LocGeofenceVO {

    private Long id;
    private Long tenantId;
    private String fenceCode;
    private String fenceName;
    private Long areaId;
    private String fenceType;
    private String geometryJson;
    private String status;
    private List<LocGeofenceRuleVO> rules;
}
