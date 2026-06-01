package com.fgroupboss.ai.psm.incidentgovernance.governance.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dw_incident_fact")
public class DwIncidentFactEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long siteId;
    private String incidentNo;
    private String incidentLevel;
    private String status;
    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
