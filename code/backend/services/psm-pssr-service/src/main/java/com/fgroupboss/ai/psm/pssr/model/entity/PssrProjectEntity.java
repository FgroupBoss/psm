package com.fgroupboss.ai.psm.pssr.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pssr_project")
public class PssrProjectEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String pssrNo;
    private String projectName;
    private String sourceType;
    private Long sourceBizId;
    private Long areaId;
    private Long equipmentId;
    private LocalDateTime plannedStartupAt;
    private String approvalStatus;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
