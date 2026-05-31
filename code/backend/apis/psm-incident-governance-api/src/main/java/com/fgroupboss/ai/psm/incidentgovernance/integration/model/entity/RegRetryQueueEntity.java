package com.fgroupboss.ai.psm.incidentgovernance.integration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reg_retry_queue")
public class RegRetryQueueEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long taskId;
    private Integer retryCount;
    private Integer maxRetries;
    private LocalDateTime nextRetryAt;
    private String lastError;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
