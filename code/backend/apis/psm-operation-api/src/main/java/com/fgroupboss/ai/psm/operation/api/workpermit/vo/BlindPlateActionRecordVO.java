package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BlindPlateActionRecordVO {

    private Long id;
    private Long workPermitId;
    private Long blindPlateId;
    private String fromStatus;
    private String toStatus;
    private String attachmentRef;
    private String operatedBy;
    private Date operatedAt;
}
