package com.fgroupboss.ai.psm.realtime.video.model.vo;

import lombok.Data;

@Data
public class VideoCameraVO {

    private Long id;
    private Long tenantId;
    private String cameraCode;
    private String cameraName;
    private String platformCode;
    private String platformCameraId;
    private Long areaId;
    private Long majorHazardId;
    private String locationDesc;
    private String status;
}
