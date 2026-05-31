package com.fgroupboss.ai.psm.operation.contractor.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkerViolationVO {

    private Long id;
    private Long tenantId;
    private Long companyId;
    private Long workerId;
    private LocalDateTime violationTime;
    private String violationDesc;
    private String severity;
    private String rectificationStatus;
}
