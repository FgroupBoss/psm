package com.fgroupboss.ai.psm.processsafety.barrier.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mi_inspection_plan")
public class MiInspectionPlanEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long equipmentId;
    private String planName;
    private Integer cycleDays;
    private LocalDateTime nextDueAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
