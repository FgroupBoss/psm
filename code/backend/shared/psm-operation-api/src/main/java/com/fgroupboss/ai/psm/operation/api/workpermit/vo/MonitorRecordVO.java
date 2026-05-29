package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MonitorRecordVO {

    private Long id;
    private String recordType;
    private String content;
    private Boolean abnormalFlag;
    private String attachmentRef;
    private String operatorName;
    private Date recordedAt;
}

