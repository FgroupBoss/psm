package com.fgroupboss.ai.psm.incidentgovernance.governance.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dw_moc_fact")
public class DwMocFactEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long siteId;
    private String changeNo;
    private String changeLevel;
    private String status;
    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
