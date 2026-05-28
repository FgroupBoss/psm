package com.fgroupboss.ai.psm.video.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VideoAiEventVO {

    private Long id;
    private Long tenantId;
    private String eventNo;
    private String sourcePlatform;
    private Long cameraId;
    private String eventType;
    private LocalDateTime eventTime;
    private Long areaId;
    private Long majorHazardId;
    private Long alarmId;
    private Long workPermitId;
    private String severity;
    private String status;
    private String title;
    private String description;
    private String ignoreReason;
    private List<VideoAiEventMediaVO> mediaItems;
}
