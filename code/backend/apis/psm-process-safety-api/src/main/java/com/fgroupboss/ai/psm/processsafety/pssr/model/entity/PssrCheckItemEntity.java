package com.fgroupboss.ai.psm.processsafety.pssr.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pssr_check_item")
public class PssrCheckItemEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long templateId;
    private String itemNo;
    private String itemDesc;
    private String discipline;
    private String issueLevel;
    private Integer startupBlockFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
