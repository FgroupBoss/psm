package com.fgroupboss.ai.psm.workpermit.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SimopsConflictRuleRequest {

    @NotNull
    private Long tenantId;
    @NotBlank
    private String workTypeA;
    @NotBlank
    private String workTypeB;
    private String areaScope;
    private Integer overlapMinutes;
    @NotBlank
    private String action;
    private Boolean enabled;
    private String remark;
}
