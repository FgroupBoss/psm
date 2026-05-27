package com.fgroupboss.ai.psm.mobile.client.vo;

import lombok.Data;

@Data
public class WorkPermitWorkerVO {

    private Long id;
    private Long workerId;
    private String workerName;
    private String workerType;
    private String qualificationStatus;
}
