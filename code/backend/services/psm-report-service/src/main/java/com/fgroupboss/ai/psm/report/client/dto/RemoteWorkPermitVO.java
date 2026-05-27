package com.fgroupboss.ai.psm.report.client.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RemoteWorkPermitVO {

    private Long id;
    private Long tenantId;
    private String permitNo;
    private String workType;
    private String status;
    private String title;
    private Long areaId;
    private Long hazardId;
    private Long contractorCompanyId;
    private Date planStartAt;
    private Date planEndAt;
    private Date actualStartAt;
    private Date actualEndAt;
    private Date createdAt;
    private Date updatedAt;
}
