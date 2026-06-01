package com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reg_report_task")
public class RegReportTaskEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String taskNo;
    private String platformCode;
    private String dataDomain;
    private String triggerType;
    private LocalDateTime dataWindowStart;
    private LocalDateTime dataWindowEnd;
    private Integer recordCount;
    private String status;
    private String payloadDigest;
    private LocalDateTime executedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
