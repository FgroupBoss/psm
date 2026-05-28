package com.fgroupboss.ai.psm.dualprevention.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dp_hazard_action")
public class HazardActionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long hazardId;
    private String actionType;
    private String beforeStatus;
    private String afterStatus;
    private String content;
    private String evidenceFileIds;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime operatedAt;
}
