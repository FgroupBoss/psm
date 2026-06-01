package com.fgroupboss.ai.psm.incidentgovernance.incident.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("incident_evidence")
public class IncidentEvidenceEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long incidentId;
    private String evidenceType;
    private Long fileId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
