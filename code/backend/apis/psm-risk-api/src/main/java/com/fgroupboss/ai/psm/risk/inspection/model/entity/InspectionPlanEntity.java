package com.fgroupboss.ai.psm.risk.inspection.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("insp_plan")
public class InspectionPlanEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String planCode;
    private String planName;
    private Long routeId;
    private String cycleType;
    private String cronExpr;
    private Long teamId;
    private String teamName;
    private Long defaultExecutorId;
    private Long majorHazardId;
    private Integer enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
