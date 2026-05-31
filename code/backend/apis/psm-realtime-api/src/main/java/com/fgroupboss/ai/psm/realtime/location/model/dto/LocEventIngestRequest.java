package com.fgroupboss.ai.psm.realtime.location.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LocEventIngestRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "eventType is required")
    private String eventType;

    private String tagNo;
    private Long personId;
    private Long areaId;
    private Long fenceId;
    private Long workPermitId;
    private String rawPayload;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime eventTime;

    private BigDecimal latitude;
    private BigDecimal longitude;
    private String onlineStatus;
}
