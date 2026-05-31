package com.fgroupboss.ai.psm.processsafety.moc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("moc_change")
public class MocChangeEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String changeNo;
    private String changeTitle;
    private String changeType;
    private String changeLevel;
    private Integer temporaryFlag;
    private Integer emergencyFlag;
    private Long affectedAreaId;
    private Long affectedEquipmentId;
    private String riskLevel;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
