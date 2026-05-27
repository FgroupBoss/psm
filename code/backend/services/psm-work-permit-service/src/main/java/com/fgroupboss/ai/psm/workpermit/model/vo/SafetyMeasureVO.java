package com.fgroupboss.ai.psm.workpermit.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SafetyMeasureVO {

    private Long id;
    private String measureCode;
    private String measureName;
    private Boolean requiredFlag;
    private String confirmStatus;
    private String confirmBy;
    private Date confirmAt;
    private String remark;
    private String attachmentRef;
}
