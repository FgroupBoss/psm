package com.fgroupboss.ai.psm.realtime.alarm.client;

import lombok.Data;

@Data
public class RemoteHazardReportVO {

    private Long id;
    private Long tenantId;
    private String hazardNo;
    private String hazardLevel;
    private String sourceType;
    private Long sourceBizId;
    private Long areaId;
    private String status;
}
