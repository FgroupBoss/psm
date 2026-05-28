package com.fgroupboss.ai.psm.workpermit.model.vo;

import lombok.Data;

@Data
public class SimopsConflictReasonVO {

    private String code;
    private String message;
    private String severity;
    private Long relatedWorkPermitId;
}
