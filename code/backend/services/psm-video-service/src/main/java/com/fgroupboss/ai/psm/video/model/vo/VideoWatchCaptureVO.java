package com.fgroupboss.ai.psm.video.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoWatchCaptureVO {

    private Long id;
    private Long sessionId;
    private Long cameraId;
    private String captureType;
    private Long fileId;
    private String mediaUrl;
    private LocalDateTime capturedAt;
    private String remark;
}
