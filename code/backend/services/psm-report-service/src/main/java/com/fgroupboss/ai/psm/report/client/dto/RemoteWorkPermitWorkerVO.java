package com.fgroupboss.ai.psm.report.client.dto;

import lombok.Data;

@Data
public class RemoteWorkPermitWorkerVO {

    private Long id;
    private String workerType;
    private Long workerId;
    private String workerName;
    private String roleCode;
    private Long companyId;
}
