package com.fgroupboss.ai.psm.incidentgovernance.governance.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gov_template_version")
public class GovTemplateVersionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long templateId;
    private String versionNo;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
