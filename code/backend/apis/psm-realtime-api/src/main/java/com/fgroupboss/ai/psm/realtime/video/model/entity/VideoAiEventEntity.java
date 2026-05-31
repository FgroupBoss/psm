package com.fgroupboss.ai.psm.realtime.video.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("video_ai_event")
public class VideoAiEventEntity {

    @TableId(type = IdType.AUTO)
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
    private String dedupKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
