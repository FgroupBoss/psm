package com.fgroupboss.ai.psm.inspection.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("insp_task")
public class InspectionTaskEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String taskNo;
    private Long planId;
    private Long routeId;
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private Long executorId;
    private String status;
    private BigDecimal completionRate;
    private Integer abnormalCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
