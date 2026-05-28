package com.fgroupboss.ai.psm.video.client.dto;

import lombok.Data;

import java.util.Date;

/**
 * 调用报警中心 ingest 的请求体（字段与 alarm-service AlarmIngestRequest 对齐）。
 */
@Data
public class RemoteAlarmIngestRequest {

    private Long tenantId;
    private String sourceType;
    private String sourceCode;
    private String title;
    private String content;
    private String alarmLevel;
    private Long areaId;
    private Long hazardId;
    private Date occurredAt;
}
