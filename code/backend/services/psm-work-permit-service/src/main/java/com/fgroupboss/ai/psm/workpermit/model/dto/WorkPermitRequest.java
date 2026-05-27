package com.fgroupboss.ai.psm.workpermit.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class WorkPermitRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;
    @NotNull(message = "workType is required")
    private String workType;
    private String title;
    private String workContent;
    private Long areaId;
    private Long unitId;
    private Long equipmentId;
    private Long hazardId;
    private Long contractorCompanyId;
    private Date planStartAt;
    private Date planEndAt;
    private Long supervisorUserId;
    private Long permitIssuerUserId;
    private Long guardianUserId;
}
