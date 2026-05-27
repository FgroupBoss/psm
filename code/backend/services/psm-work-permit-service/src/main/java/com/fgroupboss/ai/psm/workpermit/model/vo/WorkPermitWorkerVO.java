package com.fgroupboss.ai.psm.workpermit.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class WorkPermitWorkerVO {

    private Long id;
    private String workerType;
    private Long workerId;
    private String workerName;
    private String roleCode;
    private Long companyId;
    private Date createdAt;
}
