package com.fgroupboss.ai.psm.incidentgovernance.report.client.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RemoteSiteConfirmVO {

    private Long id;
    private String confirmType;
    private String confirmContent;
    private String operatorName;
    private Date confirmedAt;
}
