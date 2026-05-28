package com.fgroupboss.ai.psm.location.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LocEventVO {

    private Long id;
    private Long tenantId;
    private String eventType;
    private String tagNo;
    private Long personId;
    private Long areaId;
    private Long fenceId;
    private LocalDateTime eventTime;
    private Long alarmId;
    private Long workPermitId;
    private String rawPayload;
}
