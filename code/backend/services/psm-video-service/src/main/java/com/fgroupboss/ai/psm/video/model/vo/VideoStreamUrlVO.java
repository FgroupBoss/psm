package com.fgroupboss.ai.psm.video.model.vo;

import lombok.Data;

@Data
public class VideoStreamUrlVO {

    private Long cameraId;
    private String url;
    private long expiresAtEpochSeconds;
}
