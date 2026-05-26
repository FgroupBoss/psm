package com.fgroupboss.ai.psm.contractor.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contractor_blacklist")
public class ContractorBlacklistEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String targetType;
    private Long targetId;
    private String reason;
    private LocalDateTime effectiveAt;
    private LocalDateTime releasedAt;
    private String status;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
