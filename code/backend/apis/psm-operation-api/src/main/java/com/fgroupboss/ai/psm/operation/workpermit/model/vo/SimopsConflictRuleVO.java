package com.fgroupboss.ai.psm.operation.workpermit.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SimopsConflictRuleVO {

    private Long id;
    private Long tenantId;
    private String workTypeA;
    private String workTypeB;
    private String areaScope;
    private Integer overlapMinutes;
    private String action;
    private Boolean enabled;
    private String remark;
    private Date createdAt;
    private Date updatedAt;
}
