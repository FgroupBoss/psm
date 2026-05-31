package com.fgroupboss.ai.psm.processsafety.barrier.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mi_defect")
public class MiDefectEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String defectNo;
    private Long equipmentId;
    private String defectLevel;
    private String sourceType;
    private String description;
    private LocalDateTime repairDeadline;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
