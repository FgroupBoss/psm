package com.fgroupboss.ai.psm.location.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class GateRecordIngestRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "gateCode is required")
    private String gateCode;

    private String cardNo;
    private String tagNo;
    private Long personId;

    @NotBlank(message = "direction is required")
    private String direction;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime accessTime;

    private String accessResult;
}
