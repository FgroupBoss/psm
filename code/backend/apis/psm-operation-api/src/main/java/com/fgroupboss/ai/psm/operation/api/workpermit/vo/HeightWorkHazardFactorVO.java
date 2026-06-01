package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class HeightWorkHazardFactorVO {

    private Long id;
    private String factorCode;
    private String factorName;
    private String hitSource;
    private String controlMeasure;
    private String attachmentRef;
    private String confirmedBy;
    private Date confirmedAt;
}
