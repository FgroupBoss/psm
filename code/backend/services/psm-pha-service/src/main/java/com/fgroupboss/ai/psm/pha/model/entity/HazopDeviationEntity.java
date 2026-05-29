package com.fgroupboss.ai.psm.pha.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("hazop_deviation")
public class HazopDeviationEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long nodeId;
    private String parameter;
    private String guideword;
    private String deviationDesc;
    private String riskLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}