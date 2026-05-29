package com.fgroupboss.ai.psm.incident.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("incident_report")
public class IncidentReportEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String incidentNo;
    private String incidentType;
    private String incidentLevel;
    private LocalDateTime occurredAt;
    private Long areaId;
    private Long equipmentId;
    private String sourceType;
    private Long sourceBizId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
