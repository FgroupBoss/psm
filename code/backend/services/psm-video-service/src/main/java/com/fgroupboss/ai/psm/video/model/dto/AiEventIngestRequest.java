package com.fgroupboss.ai.psm.video.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AiEventIngestRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "sourcePlatform is required")
    private String sourcePlatform;

    private String externalEventId;

    @NotNull(message = "cameraId is required")
    private Long cameraId;

    @NotBlank(message = "eventType is required")
    private String eventType;

    @NotNull(message = "eventTime is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventTime;

    private Long areaId;
    private Long majorHazardId;
    private Long workPermitId;

    @NotBlank(message = "severity is required")
    private String severity;

    private String title;
    private String description;

    @Valid
    private List<AiEventMediaItem> mediaItems;
}
