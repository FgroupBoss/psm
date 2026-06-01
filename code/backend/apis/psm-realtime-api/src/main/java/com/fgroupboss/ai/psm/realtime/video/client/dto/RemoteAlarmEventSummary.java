package com.fgroupboss.ai.psm.realtime.video.client.dto;

import lombok.Data;

/**
 * 报警服务 ingest 返回摘要（与 alarm-service AlarmEventVO 字段对齐）。
 */
@Data
public class RemoteAlarmEventSummary {

    private Long id;
    private String alarmNo;
    private String alarmLevel;
    private String status;
}
