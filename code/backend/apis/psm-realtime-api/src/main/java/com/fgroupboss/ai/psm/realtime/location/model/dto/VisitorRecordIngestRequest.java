package com.fgroupboss.ai.psm.realtime.location.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class VisitorRecordIngestRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "visitorName is required")
    private String visitorName;

    private String idCardNo;
    private String companyName;
    private Long hostPersonId;
    private String gateCode;
    private String tagNo;
    private String visitPurpose;

    @NotBlank(message = "direction is required")
    private String direction;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime accessTime;

    private String accessResult;
}
