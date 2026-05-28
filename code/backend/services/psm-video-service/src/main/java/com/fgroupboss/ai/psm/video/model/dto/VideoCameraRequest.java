package com.fgroupboss.ai.psm.video.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class VideoCameraRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "cameraCode is required")
    private String cameraCode;

    @NotBlank(message = "cameraName is required")
    private String cameraName;

    @NotBlank(message = "platformCode is required")
    private String platformCode;

    private String platformCameraId;
    private Long areaId;
    private Long majorHazardId;
    private String locationDesc;
    private String status;
}
