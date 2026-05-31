package com.fgroupboss.ai.psm.incidentgovernance.report.client.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RemoteAlarmActionVO {

    private Long id;
    private String actionType;
    private String actionContent;
    private String operatorName;
    private Date operatedAt;
}
