package com.fgroupboss.ai.psm.location.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VisitorAccessRecordVO {

    private Long id;
    private Long tenantId;
    private String visitorName;
    private String idCardNo;
    private String companyName;
    private Long hostPersonId;
    private String gateCode;
    private String tagNo;
    private String visitPurpose;
    private String direction;
    private LocalDateTime accessTime;
    private String accessResult;
}
