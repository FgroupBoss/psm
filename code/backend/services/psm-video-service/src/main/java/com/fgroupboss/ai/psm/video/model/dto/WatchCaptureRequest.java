package com.fgroupboss.ai.psm.video.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class WatchCaptureRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private Long cameraId;

    @NotBlank(message = "captureType is required")
    private String captureType;

    private Long fileId;
    private String mediaUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime capturedAt;

    private String remark;
}
