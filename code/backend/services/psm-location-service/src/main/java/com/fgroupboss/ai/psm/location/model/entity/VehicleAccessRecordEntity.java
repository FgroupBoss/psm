package com.fgroupboss.ai.psm.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vehicle_access_record")
public class VehicleAccessRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String plateNo;
    private String vehicleType;
    private String gateCode;
    private String direction;
    private LocalDateTime accessTime;
    private String driverName;
    private LocalDateTime createdAt;
}
