package com.fgroupboss.ai.psm.risk.inspection.model.vo;

import lombok.Data;

@Data
public class AbnormalRecordVO {

    private Long id;
    private Long taskId;
    private Long taskItemId;
    private Long routePointId;
    private String abnormalDesc;
    private String photoUrls;
    private String severity;
    private String handleStatus;
    private Long hazardDraftId;
}
