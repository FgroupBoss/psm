package com.fgroupboss.ai.psm.risk.majorhazard.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("major_hazard_responsibility")
public class MajorHazardResponsibilityEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long hazardId;
    private String responsibilityType;
    private String personName;
    private String personPhone;
    private Long personId;
    private Integer sortNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
