package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ConfinedSpaceRescuePlanVO {

    private Long id;
    private String planRef;
    private String contact;
    private String equipmentJson;
    private String confirmedBy;
    private Date confirmedAt;
}
