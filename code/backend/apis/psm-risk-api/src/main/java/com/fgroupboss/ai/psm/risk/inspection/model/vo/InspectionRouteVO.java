package com.fgroupboss.ai.psm.risk.inspection.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class InspectionRouteVO {

    private Long id;
    private Long tenantId;
    private String routeCode;
    private String routeName;
    private Long areaId;
    private Integer estimatedMinutes;
    private String status;
    private String remark;
    private List<RoutePointVO> points;
}
