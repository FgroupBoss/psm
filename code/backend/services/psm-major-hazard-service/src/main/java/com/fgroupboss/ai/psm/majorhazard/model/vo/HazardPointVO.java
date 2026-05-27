package com.fgroupboss.ai.psm.majorhazard.model.vo;

import lombok.Data;

@Data
public class HazardPointVO {

    private Long id;
    private Long tenantId;
    private Long hazardId;
    private Long monitorPointId;
    private String pointCode;
    private String pointName;
}
