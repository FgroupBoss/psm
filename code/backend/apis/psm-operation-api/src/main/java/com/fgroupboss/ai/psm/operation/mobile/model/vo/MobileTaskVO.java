package com.fgroupboss.ai.psm.operation.mobile.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 移动端待办项。
 */
@Data
public class MobileTaskVO {

    private String taskType;
    private String bizType;
    private Long bizId;
    private String title;
    private String status;
    private String priority;
    private Date dueAt;
    private String actionHint;
}
