package com.fgroupboss.ai.psm.location.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class VehicleRecordIngestRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotBlank(message = "plateNo is required")
    private String plateNo;

    private String vehicleType;
    private String gateCode;

    @NotBlank(message = "direction is required")
    private String direction;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime accessTime;

    private String driverName;
}
