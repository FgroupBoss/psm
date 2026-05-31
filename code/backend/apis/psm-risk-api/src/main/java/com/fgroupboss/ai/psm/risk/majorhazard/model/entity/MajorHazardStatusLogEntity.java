package com.fgroupboss.ai.psm.risk.majorhazard.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("major_hazard_status_log")
public class MajorHazardStatusLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long hazardId;
    private String fromStatus;
    private String toStatus;
    private String reason;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime operatedAt;
}
