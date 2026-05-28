package com.fgroupboss.ai.psm.inspection.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskItemVO {

    private Long id;
    private Long checklistItemId;
    private Long routePointId;
    private String resultValue;
    private String resultStatus;
    private String photoUrls;
    private String remark;
    private LocalDateTime checkedAt;
}
