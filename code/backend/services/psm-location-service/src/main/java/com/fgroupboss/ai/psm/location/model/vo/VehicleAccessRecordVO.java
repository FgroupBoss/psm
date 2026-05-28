package com.fgroupboss.ai.psm.location.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleAccessRecordVO {

    private Long id;
    private Long tenantId;
    private String plateNo;
    private String vehicleType;
    private String gateCode;
    private String direction;
    private LocalDateTime accessTime;
    private String driverName;
}
