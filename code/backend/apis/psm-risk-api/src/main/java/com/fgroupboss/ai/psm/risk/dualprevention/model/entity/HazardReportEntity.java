package com.fgroupboss.ai.psm.risk.dualprevention.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dp_hazard_report")
public class HazardReportEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String hazardNo;
    private String hazardLevel;
    private String sourceType;
    private Long sourceBizId;
    private Long riskUnitId;
    private Long areaId;
    private String description;
    private LocalDateTime foundAt;
    private LocalDateTime rectificationDeadline;
    private String status;
    private Integer overdueFlag;
    private Long assigneeOrgId;
    private Long assigneeUserId;
    private Long contractorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
