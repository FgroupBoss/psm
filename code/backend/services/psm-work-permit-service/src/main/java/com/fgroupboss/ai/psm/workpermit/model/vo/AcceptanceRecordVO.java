package com.fgroupboss.ai.psm.workpermit.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AcceptanceRecordVO {

    private Long id;
    private String acceptanceResult;
    private String opinion;
    private String signatureText;
    private String operatorName;
    private Date acceptedAt;
}
