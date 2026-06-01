package com.fgroupboss.ai.psm.incidentgovernance.incident.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("incident_capa")
public class IncidentCapaEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long incidentId;
    private String capaNo;
    private String capaType;
    private Long ownerUserId;
    private LocalDateTime dueAt;
    private Long verificationUserId;
    private Long evidenceFileId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
