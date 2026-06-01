package com.fgroupboss.ai.psm.risk.inspection.model.vo;

import lombok.Data;

@Data
public class TaskDraftSyncResultVO {

    private String clientDraftId;
    private Long taskId;
    private String syncStatus;
    private String storage;
}
