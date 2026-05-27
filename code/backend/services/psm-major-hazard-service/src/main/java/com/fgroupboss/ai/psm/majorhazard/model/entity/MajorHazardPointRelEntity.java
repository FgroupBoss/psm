package com.fgroupboss.ai.psm.majorhazard.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("major_hazard_point_rel")
public class MajorHazardPointRelEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long hazardId;
    private Long monitorPointId;
    private String pointCode;
    private String pointName;
    private LocalDateTime createdAt;
    private Integer deleted;
}
