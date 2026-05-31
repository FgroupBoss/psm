package com.fgroupboss.ai.psm.operation.api.workpermit.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SiteConfirmVO {

    private Long id;
    private String confirmType;
    private String confirmContent;
    private String locationText;
    private String scanCode;
    private String terminalId;
    private String operatorName;
    private Date confirmedAt;
}

