package com.fgroupboss.ai.psm.video.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("video_watch_capture")
public class VideoWatchCaptureEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long sessionId;
    private Long cameraId;
    private String captureType;
    private Long fileId;
    private String mediaUrl;
    private LocalDateTime capturedAt;
    private String remark;
    private LocalDateTime createdAt;
    private Integer deleted;
}
