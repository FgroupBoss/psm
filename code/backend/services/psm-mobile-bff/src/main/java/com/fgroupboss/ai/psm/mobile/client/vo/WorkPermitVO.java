package com.fgroupboss.ai.psm.mobile.client.vo;

import lombok.Data;

import java.util.Date;

@Data
public class WorkPermitVO {

    private Long id;
    private Long tenantId;
    private String permitNo;
    private String workType;
    private String status;
    private String title;
    private String workContent;
    private Long areaId;
    private Long unitId;
    private Long equipmentId;
    private Long hazardId;
    private Long contractorCompanyId;
    private Date planStartAt;
    private Date planEndAt;
    private Date actualStartAt;
    private Date actualEndAt;
    private Long supervisorUserId;
    private Long permitIssuerUserId;
    private Long guardianUserId;
    private String rejectReason;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
