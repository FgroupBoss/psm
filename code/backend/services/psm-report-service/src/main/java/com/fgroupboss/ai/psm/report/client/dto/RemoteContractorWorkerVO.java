package com.fgroupboss.ai.psm.report.client.dto;

import lombok.Data;

@Data
public class RemoteContractorWorkerVO {

    private Long id;
    private Long tenantId;
    private Long companyId;
    private String name;
    private String accessStatus;
    private String trainingStatus;
    private String certificateStatus;
    private String status;
}
