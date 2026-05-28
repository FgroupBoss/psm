package com.fgroupboss.ai.psm.inspection.model.vo;

import lombok.Data;

@Data
public class RoutePointVO {

    private Long id;
    private Long routeId;
    private String pointCode;
    private String pointName;
    private String signType;
    private String signCode;
    private Long areaId;
    private Long checklistTemplateId;
    private Integer sortOrder;
}
