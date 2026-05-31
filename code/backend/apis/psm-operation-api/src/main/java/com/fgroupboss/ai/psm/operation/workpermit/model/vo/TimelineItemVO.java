package com.fgroupboss.ai.psm.operation.workpermit.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TimelineItemVO {

    private String itemType;
    private String action;
    private String content;
    private String operatorName;
    private Date occurredAt;
}
