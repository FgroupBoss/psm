package com.fgroupboss.ai.psm.workpermit.model.vo;

import lombok.Data;

@Data
public class SimopsConflictItemVO {

    private Long id;
    private Long scanResultId;
    private Long workPermitId;
    private Long relatedWorkPermitId;
    private String relatedPermitNo;
    private Long ruleId;
    private String workTypeA;
    private String workTypeB;
    private String action;
    private Integer overlapMinutes;
    private String message;
}
